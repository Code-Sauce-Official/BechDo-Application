package com.acash.bechdo.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acash.bechdo.R
import com.acash.bechdo.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val storage by lazy{
        FirebaseStorage.getInstance()
    }

    private val auth by lazy{
        FirebaseAuth.getInstance()
    }

    private val database by lazy{
        FirebaseFirestore.getInstance()
    }

    private var myCalendar: Calendar = Calendar.getInstance()

    private var imgType = ""

    private var downloadUrlDp:String = ""

    private lateinit var downloadUrlClgId:String

    private lateinit var dpUri:Uri

    private lateinit var clgIdUri:Uri

    private lateinit var progressDialog:ProgressDialog

    private var imgUploadCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dobEt.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR,year)
                myCalendar.set(Calendar.MONTH,month)
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                updateDate()
            }

            val datePickerDialog = DatePickerDialog(
                this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )

            val maxDateCal = Calendar.getInstance()
            maxDateCal.set(Calendar.YEAR,2003)
            maxDateCal.set(Calendar.MONTH,11)
            maxDateCal.set(Calendar.DAY_OF_MONTH,31)

            datePickerDialog.datePicker.maxDate = maxDateCal.timeInMillis
            datePickerDialog.show()
        }

        uploadPicBtn.setOnClickListener {
            checkPermissionsForImage()
            imgType = "Dp"
        }

        uploadClgIdBtn.setOnClickListener{
            checkPermissionsForImage()
            imgType = "ClgId"
        }

        nameEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkNameErrors()
            }
        }

        clgEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkClgErrors()
            }
        }

        saveBtn.setOnClickListener{
            if(noErrors()){
                progressDialog = this.createProgressDialog("Saving Data, Please wait...",false)
                progressDialog.show()
                uploadImages()
            }
        }
    }

    private fun updateDate() {
        val myFormat = "d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        dobEt.setText(sdf.format(myCalendar.time))
    }

    private fun checkPermissionsForImage() {
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),121)
        }

        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),131)
        }

        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            selectImageFromGallery()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,141)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==141){
            data?.data?.let{
                if(imgType=="Dp") {
                    dpUri = it
                    image_view.setImageURI(it)
                }else if(imgType == "ClgId"){
                    clgIdUri = it
                    uploadClgIdBtn.text = getString(R.string.uploadClgIdChanged)
                }
            }
        }
    }

    private fun checkNameErrors():Boolean {
        if(nameEt.text.isNullOrEmpty()){
            nameEt.error = "Name cannot be empty!"
            return false
        }
        return true
    }

    private fun checkClgErrors():Boolean {
        if(clgEt.text.isNullOrEmpty()){
            clgEt.error = "College cannot be empty!"
            return false
        }
        return true
    }

    private fun noErrors():Boolean {
        var noError = true

        if(!checkNameErrors()){
            noError = false
        }

        if(!checkClgErrors()){
            noError = false
        }

        if (noError) {
            if (dobEt.text.isNullOrEmpty()) {
                Toast.makeText(this, "Date of Birth cannot be empty!", Toast.LENGTH_SHORT).show()
                return false
            }

            if (yearRadio.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Year cannot be empty!", Toast.LENGTH_SHORT).show()
                return false
            }

            if (!::clgIdUri.isInitialized) {
                Toast.makeText(this, "College id cannot be empty!", Toast.LENGTH_SHORT).show()
                return false
            }

            if(!tncCheckBox.isChecked){
                Toast.makeText(this, "Accepting Terms and Conditions is mandatory to proceed!", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        } else return false
    }

    private fun uploadImages() {
        lateinit var ref:StorageReference
        lateinit var imgUri: Uri

        if(imgUploadCount==0) {
            if (::dpUri.isInitialized) {
                ref = storage.reference.child("uploads/" + auth.uid.toString() + "/Dp")
                imgUri = dpUri
            }else {
                imgUploadCount++
                uploadImages()
                return
            }
        }else {
            ref = storage.reference.child("uploads/" + auth.uid.toString() + "/ClgId")
            imgUri = clgIdUri
        }

        val bitmap: Bitmap = if(Build.VERSION.SDK_INT<=28) {
            MediaStore.Images.Media.getBitmap(contentResolver, imgUri)
        }else{
            val source = ImageDecoder.createSource(contentResolver,imgUri)
            ImageDecoder.decodeBitmap(source)
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos)
        val fileInBytes = baos.toByteArray()
        val uploadTask = ref.putBytes(fileInBytes)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (imgUploadCount == 0) {
                    downloadUrlDp = task.result.toString()
                    imgUploadCount++
                    uploadImages()
                } else if (imgUploadCount == 1) {
                    downloadUrlClgId = task.result.toString()
                    uploadDataToFirestore()
                }
            }else{
                progressDialog.dismiss()
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadDataToFirestore(){
        val user = User(
            auth.uid.toString(),
            nameEt.text.toString(),
            dobEt.text.toString(),
            clgEt.text.toString(),
            (findViewById<RadioButton>(yearRadio.checkedRadioButtonId)).text.toString(),
            downloadUrlClgId,
            downloadUrlDp
        )
        database.collection("users").document(auth.uid.toString()).set(user)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }
    }
}

fun Context.createProgressDialog(message: String, isCancelable:Boolean): ProgressDialog {
    return ProgressDialog(this).apply{
        setMessage(message)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
    }
}