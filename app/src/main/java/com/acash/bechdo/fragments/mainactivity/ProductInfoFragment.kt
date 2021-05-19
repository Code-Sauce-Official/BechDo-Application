package com.acash.bechdo.fragments.mainactivity

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.activities.createAlertDialog
import com.acash.bechdo.activities.createProgressDialog
import com.acash.bechdo.adapters.ProductPicsViewAdapter
import com.acash.bechdo.models.Product
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_product_info.view.*

class ProductInfoFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy{
        FirebaseStorage.getInstance()
    }

    private lateinit var progressDialog:ProgressDialog
    private lateinit var product:Product
    private lateinit var productJsonString:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_info, container, false)
        progressDialog = requireContext().createProgressDialog("Please Wait...",false)

        productJsonString = arguments?.getString("ProductJsonString").toString()
        val gson = Gson()
        product = gson.fromJson(productJsonString,Product::class.java)

        var price = if (product.price == 0L)
            "FREE"
        else "₹ ${product.price}"

        if(product.forRent)
            price += "/day"

        view.apply {
            tvTitle.text = product.title
            tvPrice.text = price
            tvDescription.text = product.description

            for (tag in product.tags) {
                tagsGroup.children
                    .toList()
                    .filter { (it as Chip).text == tag }
                    .forEach {
                        (it as Chip).visibility = View.VISIBLE
                    }
            }

            Glide.with(this).load(product.downLoadUrlsPics[0]).placeholder(R.drawable.defaultavatar)
                .into(largeImgView)

            val productPicsViewAdapter = ProductPicsViewAdapter(product.downLoadUrlsPics)

            productPicsViewAdapter.onClick = { position ->
                Glide.with(this).load(product.downLoadUrlsPics[position])
                    .placeholder(R.drawable.defaultavatar)
                    .into(largeImgView)
            }

            picsRview.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productPicsViewAdapter
            }

            if (product.postedBy == auth.uid.toString()) {
                btn.icon = null
                removeBtn.visibility = View.VISIBLE

                if (product.isActive) {
                    btn.text = getString(R.string.mark_as_unavailable)
                } else {
                    btn.text = getString(R.string.mark_as_available)
                }

            }

            btn.setOnClickListener {
                if (product.postedBy == auth.uid.toString()) {

                    if (product.isActive) {

                        requireContext().createAlertDialog(
                            "Confirmation",
                            "Are you sure you want to mark this product as unavailable?",
                            "Yes",
                            "No"
                        ) {
                            progressDialog.show()
                            database.collection("Products").document(product.productId)
                                .update("isActive", false)
                                .addOnSuccessListener {
                                    product.isActive = false
                                    progressDialog.dismiss()
                                    btn.text = getString(R.string.mark_as_available)
                                }
                        }

                    } else {
                        requireContext().createAlertDialog(
                            "Confirmation",
                            "Are you sure you want to mark this product as available?",
                            "Yes",
                            "No"
                        ) {
                            progressDialog.show()
                            database.collection("Products").document(product.productId)
                                .update("isActive", true)
                                .addOnSuccessListener {
                                    product.isActive = true
                                    progressDialog.dismiss()
                                    btn.text = getString(R.string.mark_as_unavailable)
                                }
                        }
                    }
                }else{
                    //Open Chat Fragment
                    
                }
            }

            removeBtn.setOnClickListener {
                requireContext().createAlertDialog(
                    "Confirmation",
                    "Are you sure you want to delete this product permanently?",
                    "Yes",
                    "No"
                ){
                    progressDialog.show()
                    deleteProductFromFirestore()
                }
            }
        }

        return view
    }

    private fun deleteProductFromFirestore() {
        database.collection("Products").document(product.productId)
            .delete()
            .addOnSuccessListener {
                deleteProductPics(0)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProductPics(currIdx:Int) {
        if(currIdx==product.downLoadUrlsPics.size){
            progressDialog.dismiss()
            Toast.makeText(requireContext(),"Product removed successfully",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).onBackPressed()
            return
        }

        val picRef = storage.getReferenceFromUrl(product.downLoadUrlsPics[currIdx])
        picRef.delete()
            .addOnSuccessListener {
                deleteProductPics(currIdx+1)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                (activity as MainActivity).onBackPressed()
            }
    }
}


