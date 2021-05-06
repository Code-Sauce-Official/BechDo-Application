package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.ProductViewHolder
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.models.Product
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.view.*
import java.util.*

class PostsFragment : Fragment() {

    private lateinit var postAdapter: FirestorePagingAdapter<Product, ProductViewHolder>
    private lateinit var useCase: String
    private lateinit var query: String

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var database : Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        query = arguments?.let{
            it.getStringArray("Task")?.get(0).toString()
        } ?: ""

        useCase = arguments?.let{
            it.getStringArray("Task")?.get(1).toString()
        } ?: "Products"

        val queryLower = query.toLowerCase(Locale.ROOT)

        when (useCase) {
            "Products" -> {
                database = FirebaseFirestore.getInstance().collection("Products")
                    .whereEqualTo("isActive", true)
                    .orderBy("titleLowerCase")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .startAt(queryLower)
                    .endAt("$queryLower~")
            }

            "Active" -> {
                view.apply {
                    tvStatus.text = getString(R.string.active_products)
                    tvStatus.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                }
                database = FirebaseFirestore.getInstance().collection("Products")
                    .whereEqualTo("isActive", true)
                    .whereEqualTo("postedBy", auth.uid.toString())
                    .orderBy("titleLowerCase")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .startAt(queryLower)
                    .endAt("$queryLower~")
            }

            "Sold" -> {
                view.apply {
                    tvStatus.text = getString(R.string.sold_products)
                    tvStatus.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                }
                database = FirebaseFirestore.getInstance().collection("Products")
                    .whereEqualTo("isActive", false)
                    .whereEqualTo("postedBy", auth.uid.toString())
                    .orderBy("titleLowerCase")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .startAt(queryLower)
                    .endAt("$queryLower~")
            }
        }

        setupAdapter()
        return view
    }

    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .build()

        val options = FirestorePagingOptions.Builder<Product>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database, config, Product::class.java)
            .build()

        postAdapter = object : FirestorePagingAdapter<Product, ProductViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
                ProductViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_product, parent, false
                    )
                )

            override fun onBindViewHolder(
                holder: ProductViewHolder,
                position: Int,
                model: Product
            ) {
                holder.bind(model)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            adapter = postAdapter
        }

        search_bar.setQuery(query,false)

        search_bar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                search_bar.clearFocus()
                val bundle = Bundle()
                bundle.putStringArray("Task", arrayOf(query, useCase))
                val fragmentToSet = PostsFragment()
                fragmentToSet.arguments = bundle
                (activity as MainActivity).setFragment(fragmentToSet)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    override fun onResume() {
        super.onResume()
        search_bar.setQuery(query,false)
    }
}