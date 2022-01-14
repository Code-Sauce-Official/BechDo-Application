package com.acash.bechdo.fragments.mainactivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.activities.*
import com.acash.bechdo.adapters.ProductPicsViewAdapter
import com.acash.bechdo.models.Product
import com.acash.bechdo.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_product_info.*
import kotlinx.android.synthetic.main.fragment_product_info.view.*

class ProductInfoFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private lateinit var progressDialog: ProgressDialog
    private lateinit var product: Product
    private lateinit var productJsonString: String
    private var favourites:ArrayList<String>? = null

    private var removePressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_info, container, false)
        progressDialog = requireContext().createProgressDialog("Please Wait...", false)

        productJsonString = arguments?.getString("ProductJsonString").toString()
        val gson = Gson()
        product = gson.fromJson(productJsonString, Product::class.java)

        var price = if (product.price == 0L)
            "FREE"
        else "₹ ${product.price}"

        if (product.forRent)
            price += "/day"

        favourites = (activity as MainActivity).currentUserInfo?.favouriteProducts

        view.apply {
            tvTitle.text = product.title
            tvPrice.text = price
            tvDescription.text = product.description
            tvClgName.text = product.clg
            btnSaveProduct.isSelected = favourites?.contains(product.productId) ?: false

            categoryChip.text = product.category

            Glide.with(this).load(product.downLoadUrlsPics[0]).placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(largeImgView)

            val productPicsViewAdapter = ProductPicsViewAdapter(product.downLoadUrlsPics)

            productPicsViewAdapter.onClick = { position ->
                Glide.with(this).load(product.downLoadUrlsPics[position])
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
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

            }else if(!product.isActive){
                tvUnavailable.visibility = View.VISIBLE
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
                                    btn?.text = getString(R.string.mark_as_available)
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
                                    btn?.text = getString(R.string.mark_as_unavailable)
                                }
                        }
                    }
                } else {
                    //Open Chat Activity
                    database.collection("users").document(product.postedBy).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                val friend = it.toObject(User::class.java)!!
                                val intent = Intent(requireContext(), ChatActivity::class.java)
                                intent.putExtra(NAME, friend.name)
                                intent.putExtra(UID, friend.uid)
                                intent.putExtra(THUMBIMG, friend.downloadUrlDp)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                }
            }

            removeBtn.setOnClickListener {
                requireContext().createAlertDialog(
                    "Confirmation",
                    "Are you sure you want to delete this product permanently?",
                    "Yes",
                    "No"
                ) {
                    progressDialog.show()
                    removePressed = true
                    deleteProductFromFirestore()
                }
            }

            btnSaveProduct.setOnClickListener {
                if (btnSaveProduct.isSelected) {
                    requireContext().createAlertDialog(
                        "Confirmation",
                        "Do you want to remove this product from Favourites?",
                        "Yes",
                        "No"
                    ) {
                        progressDialog.show()
                        removeProductFromFavourites()
                    }
                } else {
                    favourites?.let {
                        if (it.size < 10) {
                            requireContext().createAlertDialog(
                                "Confirmation",
                                "Do you want to add this product to Favourites?",
                                "Yes",
                                "No"
                            ) {
                                progressDialog.show()
                                addProductToFavourites()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "You can save at most 10 products only!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REMOVE_PRESSED,removePressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let{
            removePressed = it.getBoolean(REMOVE_PRESSED)
        }
    }

    override fun onResume() {
        super.onResume()
        if(removePressed && ::progressDialog.isInitialized && !progressDialog.isShowing) {
            activity?.onBackPressed()
        }
    }

    private fun removeProductFromFavourites() {
        database.collection("users").document(auth.uid.toString())
            .update("favouriteProducts", FieldValue.arrayRemove(product.productId))
            .addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    btnSaveProduct?.isSelected = false
                    favourites?.remove(product.productId)
                    Toast.makeText(
                        requireContext(),
                        "Product has been successfully removed from Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }

    private fun addProductToFavourites() {
        database.collection("users").document(auth.uid.toString())
            .update("favouriteProducts", FieldValue.arrayUnion(product.productId))
            .addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    btnSaveProduct?.isSelected = true
                    favourites?.add(product.productId)
                    Toast.makeText(
                        requireContext(),
                        "Product has been successfully added to Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }

    private fun deleteProductFromFirestore() {
        database.collection("Products").document(product.productId)
            .delete()
            .addOnSuccessListener {
                deleteProductPics(0)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProductPics(currIdx: Int) {
        if (currIdx == product.downLoadUrlsPics.size) {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Product removed successfully", Toast.LENGTH_SHORT)
                .show()
            activity?.onBackPressed()
            return
        }

        val picRef = storage.getReferenceFromUrl(product.downLoadUrlsPics[currIdx])
        picRef.delete()
            .addOnSuccessListener {
                deleteProductPics(currIdx + 1)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
    }

    companion object {
        private const val REMOVE_PRESSED = "remove_pressed"
    }
}



