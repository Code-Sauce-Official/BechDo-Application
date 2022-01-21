package com.acash.bechdo.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_sell_products.*
import kotlinx.android.synthetic.main.fragment_sell_products.view.*

class SellProductsFragment : Fragment() {
    private lateinit var fragmentToSet: Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sell_products, container, false)

        Glide.with(requireContext()).load(R.drawable.sell_products_card_bg)
            .error(R.drawable.default_image)
            .into(view.imgViewSellProducts)

        Glide.with(requireContext()).load(R.drawable.rent_products_card_bg)
            .error(R.drawable.default_image)
            .into(view.imgViewRentProducts)

        Glide.with(requireContext()).load(R.drawable.active_products_card_bg)
            .error(R.drawable.default_image)
            .into(view.imgViewActvProducts)

        Glide.with(requireContext()).load(R.drawable.sold_products_card_bg)
            .error(R.drawable.default_image)
            .into(view.imgViewSoldProducts)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardSellProducts.setOnClickListener {
            fragmentToSet = PostProductsFragment()
            val bundle = Bundle()
            bundle.putBoolean("Rent", false)
            fragmentToSet.arguments = bundle
            (activity as MainActivity).setFragment(fragmentToSet)
        }

        cardRentProducts.setOnClickListener {
            fragmentToSet = PostProductsFragment()
            val bundle = Bundle()
            bundle.putBoolean("Rent", true)
            fragmentToSet.arguments = bundle
            (activity as MainActivity).setFragment(fragmentToSet)
        }

        cardActiveProducts.setOnClickListener {
            fragmentToSet = PostsFragment()
            val bundle = Bundle()
            bundle.putString("Task","Active")
            fragmentToSet.arguments = bundle
            (activity as MainActivity).setFragment(fragmentToSet)
        }

        cardSoldProducts.setOnClickListener {
            fragmentToSet = PostsFragment()
            val bundle = Bundle()
            bundle.putString("Task","Sold")
            fragmentToSet.arguments = bundle
            (activity as MainActivity).setFragment(fragmentToSet)
        }
    }

}