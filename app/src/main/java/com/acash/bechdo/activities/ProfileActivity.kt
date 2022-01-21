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
import android.text.method.LinkMovementMethod
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.acash.bechdo.R
import com.acash.bechdo.models.Colleges
import com.acash.bechdo.models.User
import com.acash.bechdo.utils.getCurrentLocale
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

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

    private lateinit var selectImgLauncher: ActivityResultLauncher<Intent>

    private var myCalendar: Calendar = Calendar.getInstance()

    private lateinit var datePickerDialog: DatePickerDialog

    private var downloadUrlDp:String = ""

    private lateinit var dpUri:Uri

    private lateinit var progressDialog:ProgressDialog

    private lateinit var clgSet:HashSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        selectImgLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        dpUri = uri

                        Glide.with(this).load(uri).placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(image_view)
                    }
                }
            }

        tvTnC.movementMethod = LinkMovementMethod.getInstance()

        val clgList = ArrayList<String>()

        val clgAdapter = ArrayAdapter(
            this,
            R.layout.list_item_dropdown_menu,
            R.id.tvClgName,
            clgList
        )

        clgDropDown.setAdapter(clgAdapter)

        database.collection("Colleges").document("Names")
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
                    val colleges = it.toObject(Colleges::class.java)
                    colleges?.apply {
                        clgList.clear()
                        clgList.addAll(listOfColleges)
                        clgList.sort()
                        clgSet = HashSet(clgList)
                        clgAdapter.notifyDataSetChanged()

                        if(clgInput?.isErrorEnabled == true && !clgDropDown?.text.isNullOrEmpty()){
                            clgInput?.isErrorEnabled = false
                        }
                    }
                }
            }

        dobEt.setOnClickListener {
            if (!(::datePickerDialog.isInitialized)) {
                createDatePickerDialog()
            }
            datePickerDialog.show()
        }

        uploadPicBtn.setOnClickListener {
            checkPermissionsForImage()
        }

        nameEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkNameErrors()
            }
        }

        clgDropDown.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkClgErrors()
            }
        }

        nameEt.addTextChangedListener {
            nameInput.isErrorEnabled = false
        }

        clgDropDown.addTextChangedListener {
            clgInput.isErrorEnabled = false
        }

        saveBtn.setOnClickListener{
            if(noErrors()){
                progressDialog = this.createProgressDialog("Saving Data, Please wait...",false)
                progressDialog.show()

                if (::dpUri.isInitialized)
                    uploadDp()
                else uploadDataToFirestore()
            }
        }
    }

    private fun createDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    private fun updateDate() {
        val myFormat = "d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, getCurrentLocale(this))
        dobEt.setText(sdf.format(myCalendar.time))
    }

    private fun checkPermissionsForImage() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        )
            selectImageFromGallery()
        else requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 121
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 121) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery()
            } else {
                Toast.makeText(
                    this,
                    "Cannot select image without storage permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        if(::selectImgLauncher.isInitialized)
            selectImgLauncher.launch(intent)
    }

    private fun checkNameErrors():Boolean {
        if(nameEt.text.isNullOrEmpty()){
            nameInput.error = "Name cannot be empty!"
            return false
        }
        return true
    }

    private fun checkClgErrors():Boolean {

        if(clgDropDown.text.isNullOrEmpty()){
            clgInput.error = "College cannot be empty!"
            return false
        }

        if(!(::clgSet.isInitialized)){
            clgInput.error = "Waiting for College list to load"
            return false
        }

        if(!clgSet.contains(clgDropDown.text.toString())){
            clgInput.error = "Please select a college from the given list.."
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

            if(!tncCheckBox.isChecked){
                Toast.makeText(this, "Accepting Terms and Conditions is mandatory to proceed!", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }

        return false
    }

    private fun uploadDp() {
        val ref = storage.reference.child("uploads/" + auth.uid.toString() + "/Dp")

        val bitmap: Bitmap = if (Build.VERSION.SDK_INT <= 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, dpUri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, dpUri)
            ImageDecoder.decodeBitmap(source)
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
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
                downloadUrlDp = task.result.toString()
                uploadDataToFirestore()
            } else {
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
            clgDropDown.text.toString(),
            (findViewById<RadioButton>(yearRadio.checkedRadioButtonId)).text.toString(),
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