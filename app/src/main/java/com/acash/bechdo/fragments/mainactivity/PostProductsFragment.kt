package com.acash.bechdo.fragments.mainactivity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.activities.createProgressDialog
import com.acash.bechdo.adapters.ProductPicsAdapter
import com.acash.bechdo.models.Product
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_post_products.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

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

    private val selectedTags = ArrayList<String>()
    private val pics = ArrayList<Uri>()
    private lateinit var productPicsAdapter:ProductPicsAdapter
    private var forRent = false
    private var storageLocation = "uploads/" + auth.uid.toString() + "/Products Posted/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_products, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if(pics.size==7) {
                    pics.clear()
                }

                result.data?.apply {
                    clipData?.let { clipData ->
                        for (i in 0 until clipData.itemCount) {
                            val item = clipData.getItemAt(i)

                            if (pics.size == 7) {
                                break
                            }

                            pics.add(item.uri)
                        }
                    }?:data?.let { uri->
                        pics.add(uri)
                    }
                }

                productPicsAdapter.notifyDataSetChanged()

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.getBoolean("Rent")==true){
            tvHeading.text = getString(R.string.rent_info)
            tvPrice.text = getString(R.string.price_per_day)
            forRent = true
            storageLocation += "For Rent/"
        }else storageLocation += "For Sale/"

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
                progressDialog = requireContext().createProgressDialog(
                    "Saving Data, Please wait...",
                    false
                )
                progressDialog.show()
                productId = database.collection("Products").document().id
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
            Toast.makeText(
                requireContext(),
                "You need to upload at-least one image of the product!",
                Toast.LENGTH_SHORT
            ).show()
        }else{

            tagsGroup.children
                .toList()
                .filter { (it as Chip).isChecked }
                .forEach {
                    selectedTags.add((it as Chip).text.toString())
                }

            if(selectedTags.isEmpty()){
                rv=false
                Toast.makeText(
                    requireContext(),
                    "You need to select at-least one tag",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return rv
    }

    private fun uploadPics(currIdx: Int) {
        if(currIdx==pics.size){
            uploadDataToFirestore()
            return
        }

        val bitmap:Bitmap = if(Build.VERSION.SDK_INT<=28) {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, pics[currIdx])
        }else{
            val source = ImageDecoder.createSource(requireContext().contentResolver, pics[currIdx])
            ImageDecoder.decodeBitmap(source)
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val fileInBytes = baos.toByteArray()

        val ref = storage.reference.child(storageLocation + "${productId}/Image_${currIdx}")
        val uploadTask = ref.putBytes(fileInBytes)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrls.add(task.result.toString())
                uploadPics(currIdx + 1)
            }else{
                progressDialog.dismiss()
                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadDataToFirestore() {
        var price = priceEt.text.toString()

        price = if (price.toLong() == 0L)
            "FREE"
        else "₹ $price"

        if(forRent){
            selectedTags.add("Rent")
            price+="/day"
        }

        val product = Product(
            auth.uid.toString(),
            titleEt.text.toString(),
            titleEt.text.toString().toLowerCase(Locale.ROOT),
            descriptionEt.text.toString(),
            price,
            downloadUrls,
            selectedTags,
            forRent
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
            activity?.requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                121
            )
        }

        if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            activity?.requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                131
            )
        }

        if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        )
            selectMultipleImagesFromGallery()
    }

    private fun selectMultipleImagesFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "Select images(at max 7)"))
    }
}