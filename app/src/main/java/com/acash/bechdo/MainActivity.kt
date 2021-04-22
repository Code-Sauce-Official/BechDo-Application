package com.acash.bechdo

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.acash.bechdo.fragments.mainactivity.HomeFragment
import com.acash.bechdo.fragments.mainactivity.PostProductsFragment
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,DrawerLayout.DrawerListener {

    private var fragmentToSet:Fragment = HomeFragment()
    private var currFragment = 0
    private var navItemSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userNameDp = intent.getStringArrayExtra(CURRENT_USER)
        userNameDp?.let{
            tvUserName.text = it[0]
            Picasso.get().load(it[1]).placeholder(R.drawable.defaultavatar).error(R.drawable.defaultavatar).into(dp)
        }

        options.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

        navigation_view.setNavigationItemSelectedListener(this)

        drawer_layout.addDrawerListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navItemSelected = true

        when (item.itemId) {

            R.id.home -> {
                fragmentToSet = HomeFragment()
                currFragment = 0
            }

            R.id.sellProducts -> {
                fragmentToSet = PostProductsFragment()
                currFragment = 2
            }

            else -> {
                fragmentToSet = HomeFragment()
                currFragment = 0
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    override fun onDrawerOpened(drawerView: View) {}

    override fun onDrawerClosed(drawerView: View) {
        if(navItemSelected) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragmentToSet)
                .commit()
            navItemSelected = false
        }
    }

    override fun onDrawerStateChanged(newState: Int) {}

    override fun onBackPressed() {
        if(currFragment==0){
            super.onBackPressed()
        }else changeFragment(0)
    }

    fun changeFragment(index:Int){
        navigation_view.menu.getItem(index).isChecked = true
        onNavigationItemSelected(navigation_view.menu.getItem(index))
        onDrawerClosed(navigation_view)
    }
}
