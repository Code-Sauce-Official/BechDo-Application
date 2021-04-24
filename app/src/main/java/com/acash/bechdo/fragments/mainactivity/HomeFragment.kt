package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.R
import com.acash.bechdo.adapters.CategoryAdapter
import com.acash.bechdo.adapters.RecentAdapter
import com.acash.bechdo.models.Categories
import com.acash.bechdo.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val database by lazy{
        FirebaseFirestore.getInstance()
    }

    private val recents = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityRef = requireContext()

        val categories = Categories.getCategories()
        val categoryAdapter = CategoryAdapter(categories, activityRef)

        rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(0)
            adapter = categoryAdapter
        }

        val recentAdapter = RecentAdapter(recents)

        rvRecent.apply{
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
            scrollToPosition(recents.size-1)
            adapter = recentAdapter
        }

        database.collection("Products").get()
            .addOnSuccessListener {queryDocumentSnapshots->
                if(!queryDocumentSnapshots.isEmpty) {
                    for (snapshot in queryDocumentSnapshots) {
                        recents.add(snapshot.toObject(Product::class.java))
                    }
                    recentAdapter.notifyDataSetChanged()
                    rvRecent.scrollToPosition(recents.size-1)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }

        btnAddProduct.setOnClickListener {
            (activity as MainActivity).changeFragment(2)
        }

    }
}