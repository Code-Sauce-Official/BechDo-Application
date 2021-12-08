package com.acash.bechdo.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.acash.bechdo.BuildConfig
import com.acash.bechdo.R
import com.acash.bechdo.activities.ChatActivity
import com.acash.bechdo.activities.NAME
import com.acash.bechdo.activities.THUMBIMG
import com.acash.bechdo.activities.UID
import com.acash.bechdo.models.Inbox
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class NotificationWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val rtDb by lazy {
        FirebaseDatabase.getInstance(BuildConfig.RTDB_URL)
    }

    override fun doWork(): Result {
        if (auth.currentUser != null) {
            var isSuccessful = false

            val task = rtDb.reference.child("inbox/${auth.uid.toString()}").get()

            Tasks.await(task)

            val inboxListSnapshot = task.result

            if (inboxListSnapshot?.exists() == true) {
                for (inboxSnapshot in inboxListSnapshot.children) {
                    val friendInInbox = inboxSnapshot.getValue(Inbox::class.java)
                    friendInInbox?.apply {
                        if (count > 0) {
                            showNotification(this)
                        }
                    }
                }
                isSuccessful = true
            }

            return if (isSuccessful)
                Result.success()
            else Result.retry()
        }
        return Result.retry()
    }


    private fun showNotification(friendInInbox:Inbox) {
        val nm: NotificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, ChatActivity::class.java)
        friendInInbox.apply {
            intent.putExtra(NAME, name)
            intent.putExtra(UID, from)
            intent.putExtra(THUMBIMG, image)
            intent.data = Uri.parse("scheme:///$from")
        }

        val pi = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    "Notifications",
                    "New message",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                    enableVibration(true)
                }
            )
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, "Notifications")
        } else {
            NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title =
            with(friendInInbox) {
                if (count == 1) "$count new message from $name"
                else "$count new messages from $name"
            }

        var largeIcon = BitmapFactory.decodeResource(context.resources,R.drawable.default_avatar)

        if(friendInInbox.image!="") {
            runBlocking(Dispatchers.IO){
                largeIcon = try {
                    val url = URL(friendInInbox.image)
                    val httpUrlConnection = url.openConnection() as HttpURLConnection
                    val inputStream = httpUrlConnection.inputStream
                    BitmapFactory.decodeStream(inputStream)
                }catch (e:MalformedURLException){
                    BitmapFactory.decodeResource(context.resources,R.drawable.default_avatar)
                } catch (e:IOException){
                    BitmapFactory.decodeResource(context.resources,R.drawable.default_avatar)
                }
            }
        }

        val newMsgNotification = builder
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(friendInInbox.msg)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(friendInInbox.msg))
            .build()

        nm.notify(friendInInbox.from,0, newMsgNotification)
    }
}