package com.acash.bechdo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.BuildConfig
import com.acash.bechdo.R
import com.acash.bechdo.adapters.ChatAdapter
import com.acash.bechdo.models.*
import com.acash.bechdo.utils.isSameDayAs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*

const val NAME = "name"
const val UID = "uid"
const val THUMBIMG = "thumbImg"

class ChatActivity : AppCompatActivity() {
    private val friendId by lazy{
        intent.getStringExtra(UID)!!
    }

    private val friendName by lazy{
        intent.getStringExtra(NAME)!!
    }

    private val friendImg by lazy{
        intent.getStringExtra(THUMBIMG)!!
    }

    private val currentUid by lazy{
        FirebaseAuth.getInstance().uid.toString()
    }

    private val db by lazy{
        FirebaseDatabase.getInstance(BuildConfig.RTDB_URL)
    }

    private var listChatEvents = mutableListOf<ChatEvent>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUser: User
    private lateinit var updateUnreadCountListener:ValueEventListener
    private lateinit var userStatusListener: ValueEventListener
    private lateinit var msgListener:ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)

        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameTv.text = friendName

        if(friendImg!="")
            Glide.with(this).load(friendImg).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(userImgView)

        chatAdapter = ChatAdapter(listChatEvents,currentUid)

        msgRv.apply{
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        chatAdapter.likeMsg= {msgId, isLiked ->  
            getMessages(friendId).child(msgId).child("liked").setValue(isLiked)
        }

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener{
            emojiPopup.toggle()
        }

        FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
            .addOnSuccessListener{
                currentUser = it.toObject(User::class.java)!!
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                finish()
            }

        sendBtn.setOnClickListener{
            msgEdtv.text?.let {
                if(it.isNotEmpty() && this::currentUser.isInitialized) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenMessages()
        getUserStatus()
        updateUnreadCount()
    }

    private fun getUserStatus() {
        userStatusListener = db.reference.child("user_status/${friendId}").addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    onlineTv.visibility = View.VISIBLE
                }else{
                    onlineTv.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity,"Failed to get User Status:${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun listenMessages() {

        if(listChatEvents.size>0){
            listChatEvents.clear()
            chatAdapter.notifyDataSetChanged()
        }

        msgListener = getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msgMap = snapshot.getValue(Messages::class.java)!!
                    addMessage(msgMap)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val msgMap = snapshot.getValue(Messages::class.java)!!
                    updateMessage(msgMap)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity,"Failed to listen messages:${error.message}",Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateMessage(msgMap: Messages) {
        val position = listChatEvents.indexOfFirst {
            when(it){
                is Messages -> it.msgId == msgMap.msgId
                else -> false
            }
        }

        listChatEvents[position] = msgMap
        chatAdapter.notifyItemChanged(position)
    }

    private fun addMessage(msgMap: Messages) {
        val eventBefore = listChatEvents.lastOrNull()
        if((eventBefore!=null) && !eventBefore.sentAt.isSameDayAs(msgMap.sentAt) || (eventBefore==null)){
            listChatEvents.add(
                DateHeader(this,msgMap.sentAt)
            )
        }

        listChatEvents.add(msgMap)
        chatAdapter.notifyItemInserted(listChatEvents.size-1)
        msgRv.scrollToPosition(listChatEvents.size-1)
    }

    private fun sendMessage(msg:String) {
        val id = getMessages(friendId).push().key
        checkNotNull(id){"Cannot be null"}
        val msgMap = Messages(msg,currentUid,id)
        getMessages(friendId).child(id).setValue(msgMap)
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
            }
        updateLastMsg(msgMap)
    }

    private fun updateLastMsg(msgMap: Messages) {
        val inboxMap = Inbox(msgMap.msg, friendId, friendName, friendImg, 0)
        getInbox(currentUid, friendId).setValue(inboxMap)
            .addOnSuccessListener {
                getInbox(friendId, currentUid).addListenerForSingleValueEvent(object :
                    ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue(Inbox::class.java)
                        inboxMap.apply {
                            from = currentUid
                            name = currentUser.name
                            image = currentUser.downloadUrlDp
                            count = 1
                        }

                        value?.let {
                            inboxMap.count = value.count + 1
                        }
                        getInbox(friendId, currentUid).setValue(inboxMap)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@ChatActivity,
                            "Failed to update Friend's inbox:${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUnreadCount(){
        updateUnreadCountListener = getInbox(currentUid,friendId).addValueEventListener(object:
            ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    getInbox(currentUid,friendId).child("count").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity,"Failed to update Unread Count:${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMessages(friend_id: String) =
        db.reference.child("messages/${getId(friend_id)}")

    private fun getInbox(toUser:String,fromUser:String) =
        db.reference.child("inbox/$toUser/$fromUser")

    private fun getId(friend_id:String):String =
        if(friend_id>currentUid)
            currentUid+friend_id
        else friend_id+currentUid

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        if(::updateUnreadCountListener.isInitialized)
            getInbox(currentUid,friendId).removeEventListener(updateUnreadCountListener)

        if(::userStatusListener.isInitialized)
            db.reference.child("user_status/${friendId}").removeEventListener(userStatusListener)

        if(::msgListener.isInitialized)
            getMessages(friendId).removeEventListener(msgListener)
    }
}