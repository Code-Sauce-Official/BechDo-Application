package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.adapters.CategoryAdapter
import com.acash.bechdo.adapters.RecentAdapter
import com.acash.bechdo.models.Categories
import com.acash.bechdo.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val database by lazy{
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityRef = requireActivity()

        val categories = Categories.getCategories()
        val categoryAdapter = CategoryAdapter(categories, activityRef)

        rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(0)
            adapter = categoryAdapter
        }

        val recents = ArrayList<Product>()
        val recentAdapter = RecentAdapter(recents)

        rvRecent.apply{
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(0)
            adapter = recentAdapter
        }

        database.collection("Products").whereEqualTo("isActive",true).orderBy("createdDate",Query.Direction.DESCENDING).limit(10).get()
            .addOnSuccessListener {queryDocumentSnapshots->
                if(!queryDocumentSnapshots.isEmpty) {
                    for (snapshot in queryDocumentSnapshots) {
                        recents.add(snapshot.toObject(Product::class.java))
                    }
                    recentAdapter.notifyDataSetChanged()
                    rvRecent?.scrollToPosition(0)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }

        btnShowAll.setOnClickListener {
            (activity as MainActivity).changeFragmentFromDrawer(1)
        }

        btnAddProduct.setOnClickListener {
            (activity as MainActivity).changeFragmentFromDrawer(2)
        }

        search_bar.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                search_bar.setQuery("",false)
                search_bar.clearFocus()
                val  bundle = Bundle()
                bundle.putString("Task", "Products")
                bundle.putString("Query",query)
                (activity as MainActivity).changeFragmentFromDrawer(1,bundle)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }
}
