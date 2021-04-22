package com.acash.bechdo.fragments.mainactivity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.adapters.ProductPicsAdapter
import com.acash.bechdo.createProgressDialog
import com.acash.bechdo.models.Product
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_post_products.*

class PostProductsFragment : Fragment() {
    private val storage by lazy{
        FirebaseStorage.getInstance()
    }

    private val auth by lazy{
        FirebaseAuth.getInstance()
    }

    private val database by lazy{
        FirebaseFirestore.getInstance()
    }

    private lateinit var resultLauncher:ActivityResultLauncher<Intent>
    private lateinit var productId:String
    private lateinit var progressDialog: ProgressDialog
    private val downloadUrls = ArrayList<String>()

    private val tags =
        arrayListOf("Instruments", "Stationery", "Electronics", "Sports", "Books", "Rent", "Other")
    private val pics = ArrayList<Uri>()
    private lateinit var productPicsAdapter:ProductPicsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_products, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if(pics.size>=7) {
                    pics.clear()
                }

                result.data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)

                        if(i>6){
                            break
                        }

                        pics.add(item.uri)
                    }
                    productPicsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, tags)
        spinnerTags.adapter = spinnerAdapter
        productPicsAdapter = ProductPicsAdapter(pics, requireContext())
        productPicsAdapter.onClick = {
            checkPermissionsForImage()
        }

        picsRview.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 4, LinearLayoutManager.VERTICAL, false)
            adapter = productPicsAdapter
        }

        postBtn.setOnClickListener {
            if(isAcceptableInput()){
                progressDialog = requireContext().createProgressDialog("Saving Data, Please wait...",false)
                progressDialog.show()
                productId = "PRD_${System.currentTimeMillis()}"
                uploadPics(0)
            }
        }

        cancelBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun isAcceptableInput(): Boolean {
        var rv = true
        if(titleEt.text.isNullOrEmpty()){
            rv=false
            titleEt.error = "Title cannot be empty"
        }

        if(descriptionEt.text.isNullOrEmpty()){
            rv=false
            descriptionEt.error = "Description cannot be empty"
        }

        if(priceEt.text.isNullOrEmpty()){
            rv=false
            priceEt.error = "Price cannot be empty"
        }

        if(!rv){
            return rv
        }

        if(pics.isEmpty()){
            rv=false
            Toast.makeText(requireContext(),"You need to upload at-least one image of the product!",Toast.LENGTH_SHORT).show()
        }

        return rv
    }

    private fun uploadPics(currIdx:Int) {
        if(currIdx==pics.size){
            uploadDataToFirestore()
            return
        }

        val ref = storage.reference.child("uploads/" + auth.uid.toString() + "/Products Posted/${productId}/Image_${currIdx}")
        val uploadTask = ref.putFile(pics[currIdx])

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrls.add(task.result.toString())
                uploadPics(currIdx+1)
            }else{
                progressDialog.dismiss()
                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadDataToFirestore() {
        val product = Product(
            auth.uid.toString(),
            titleEt.text.toString(),
            descriptionEt.text.toString(),
            priceEt.text.toString(),
            downloadUrls,
            spinnerTags.selectedItem.toString()
        )

        database.collection("Products").document(productId).set(product)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT)
                    .show()
                activity?.onBackPressed()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissionsForImage() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            activity?.requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 121)
        }

        if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            activity?.requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 131)
        }

        if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        )
            selectMultipleImagesFromGallery()
    }

    private fun selectMultipleImagesFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(Intent.createChooser(intent,"Select images(at max 7)"))
    }
}