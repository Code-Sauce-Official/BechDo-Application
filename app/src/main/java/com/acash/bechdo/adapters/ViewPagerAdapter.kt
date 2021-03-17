package com.acash.bechdo.adapters

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class ViewPagerAdapter(fa:FragmentActivity, var fragments:ArrayList<Fragment>) : FragmentStateAdapter(fa) {

    override fun getItemCount()=fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}