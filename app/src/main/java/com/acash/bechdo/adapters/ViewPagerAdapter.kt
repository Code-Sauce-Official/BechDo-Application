package com.acash.bechdo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa:FragmentActivity, private val fragments:ArrayList<Fragment>) : FragmentStateAdapter(fa) {

    override fun getItemCount()=fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}