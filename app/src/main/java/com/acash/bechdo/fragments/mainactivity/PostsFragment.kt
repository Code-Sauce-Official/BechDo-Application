package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.models.Product
import com.acash.bechdo.viewholders.ProductViewHolder
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PostsFragment : Fragment() {

    private lateinit var postAdapter: FirestorePagingAdapter<Product, ProductViewHolder>
    lateinit var task: String
    private lateinit var query: String
    lateinit var newQuery: String
    lateinit var category: String
    var type = -1
    lateinit var priceRange:String
    lateinit var clg:String

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var database: Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        task = arguments?.getString("Task") ?: "Products"

        query = arguments?.getString("Query") ?: ""

        category = arguments?.getString("CategoryFilter") ?: ""

        type = arguments?.getInt("Type",-1) ?: -1

        priceRange = arguments?.getString("PriceRange") ?: ""

        clg = arguments?.getString("College") ?: ""

        newQuery = query

        val queryLower = query.lowercase(Locale.ROOT)

        when (task) {
            "Products" -> {
                database = if (query == "") {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", true)
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                } else {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", true)
                        .orderBy("titleLowerCase")
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .startAt(queryLower)
                        .endAt("$queryLower~")
                }
            }

            "Category" ->{
                view.apply {
                    tvStatus.text = category
                    tvStatus.visibility = View.VISIBLE
                }

                setFilters(queryLower)
            }

            "Favourites" -> {
                view.apply {
                    tvStatus.text = getString(R.string.favourites)
                    tvStatus.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                }

                val favouritesUserRef = (activity as MainActivity).currentUserInfo?.favouriteProducts

                val favourites = favouritesUserRef?.let{
                    ArrayList(it)
                } ?: ArrayList()

                if(favourites.size>0)
                    updateFavourites(favourites)
                else favourites.add("")

                database = if (query == "") {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereIn("productId", favourites)
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                }else{
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereIn("productId", favourites)
                        .orderBy("titleLowerCase")
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .startAt(queryLower)
                        .endAt("$queryLower~")
                }
            }

            "Filters" -> {
                setFilters(queryLower)
            }

            "Active" -> {
                view.apply {
                    tvStatus.text = getString(R.string.active_products)
                    tvStatus.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                }

                database = if (query == "") {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", true)
                        .whereEqualTo("postedBy", auth.uid.toString())
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                } else {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", true)
                        .whereEqualTo("postedBy", auth.uid.toString())
                        .orderBy("titleLowerCase")
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .startAt(queryLower)
                        .endAt("$queryLower~")
                }
            }

            "Sold" -> {
                view.apply {
                    tvStatus.text = getString(R.string.sold_products)
                    tvStatus.visibility = View.VISIBLE
                    filterBtn.visibility = View.GONE
                }

                database = if (query == "") {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", false)
                        .whereEqualTo("postedBy", auth.uid.toString())
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                } else {
                    FirebaseFirestore.getInstance().collection("Products")
                        .whereEqualTo("isActive", false)
                        .whereEqualTo("postedBy", auth.uid.toString())
                        .orderBy("titleLowerCase")
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .startAt(queryLower)
                        .endAt("$queryLower~")
                }
            }
        }

        setupAdapter()
        return view
    }

    private fun setFilters(queryLower:String){
        database = FirebaseFirestore.getInstance().collection("Products")
            .whereEqualTo("isActive",true)

        if(type!=-1 && type!=2){
            var forRent = false
            if(type==0){
                forRent = true
            }

            database = database.whereEqualTo("forRent",forRent)
        }

        if(category!=""){
            database = database.whereEqualTo("category",category)
        }

        if(priceRange!=""){
            val priceRangeIndex: Int = when {
                priceRange[0] == '0' -> {
                    0
                }
                priceRange[0] == '1' -> {
                    5
                }
                else -> {
                    (Integer.parseInt(priceRange.substring(0, 3))) / 200
                }
            }
            database = database.whereEqualTo("priceFilterIndex",priceRangeIndex)
        }

        if(clg!=""){
            database = database.whereEqualTo("clg",clg)
        }

        database = database.orderBy("titleLowerCase")
            .startAt(queryLower)
            .endAt("$queryLower~")

    }

    private fun updateFavourites(favourites:ArrayList<String>) {
        FirebaseFirestore.getInstance().collection("Products")
            .whereIn("productId", favourites)
            .get()
            .addOnSuccessListener { productListSnapshot->
                val favouriteProductIds:ArrayList<String> = (activity as MainActivity).currentUserInfo?.favouriteProducts ?: ArrayList()

                if (productListSnapshot.size() != favouriteProductIds.size) {
                    val listProducts = productListSnapshot.toObjects(Product::class.java)
                    favouriteProductIds.clear()

                    for (product in listProducts) {
                        favouriteProductIds.add(product.productId)
                    }

                    FirebaseFirestore.getInstance()
                        .collection("users").document(auth.uid.toString())
                        .update(
                            mapOf(
                                "favouriteProducts" to favouriteProductIds
                            )
                        )
                }
            }
    }

    private fun setupAdapter() {

        val config = PagedList.Config.Builder()
            .setPageSize(8)
            .setInitialLoadSizeHint(16)
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
                holder.bind(model){ productJsonString->
                    val fragmentToSet = ProductInfoFragment()
                    val bundle = Bundle()
                    bundle.putString("ProductJsonString",productJsonString)
                    fragmentToSet.arguments = bundle
                    (activity as MainActivity).setFragment(fragmentToSet)
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Toast.makeText(requireContext(),"Failed to fetch posts",Toast.LENGTH_SHORT).show()
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

        search_bar.setQuery(query, false)

        search_bar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                search_bar.clearFocus()
                val bundle = Bundle()
                bundle.putString("Task",task)
                bundle.putString("Query",query)

                if(task=="Category" || task=="Filters"){
                    bundle.putString("CategoryFilter",category)
                    bundle.putInt("Type",type)
                    bundle.putString("PriceRange",priceRange)
                    bundle.putString("College",clg)
                }

                val fragmentToSet = PostsFragment()
                fragmentToSet.arguments = bundle
                (activity as MainActivity).setFragment(fragmentToSet)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    newQuery = it
                }
                return false
            }
        })

        filterBtn.setOnClickListener {
            search_bar.clearFocus()
            val filtersFragment = FiltersFragment()
            filtersFragment.show(childFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        search_bar.setQuery(query, false)
    }
}