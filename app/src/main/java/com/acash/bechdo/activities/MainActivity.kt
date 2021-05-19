package com.acash.bechdo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.acash.bechdo.R
import com.acash.bechdo.fragments.mainactivity.EditProfileFragment
import com.acash.bechdo.fragments.mainactivity.HomeFragment
import com.acash.bechdo.fragments.mainactivity.PostsFragment
import com.acash.bechdo.fragments.mainactivity.SellProductsFragment
import com.acash.bechdo.models.User
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_product_info.view.*
import java.util.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,DrawerLayout.DrawerListener {

    private var fragmentToSet:Fragment = HomeFragment()
    private var wantToChangeFragment = false
    private var signOutPressed = false
    var currentUserInfo: User? = null
    private var navDrawerBackStackIndices = Stack<Int>()
    private var currentFragment = 0
    private var nextFragment = 0

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database.collection("users").document(auth.uid.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    currentUserInfo = it.toObject(User::class.java)
                    setName()
                    setDp()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                finish()
            }

        options.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

        dp.setOnClickListener {
            setFragment(EditProfileFragment())
        }

        navigation_view.setNavigationItemSelectedListener(this)

        drawer_layout.addDrawerListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        wantToChangeFragment = true

        when (item.itemId) {

            R.id.home -> {
                fragmentToSet = HomeFragment()
                nextFragment = 0
            }

            R.id.products -> {
                fragmentToSet = PostsFragment()
                nextFragment = 1
            }

            R.id.sellProducts -> {
                fragmentToSet = SellProductsFragment()
                nextFragment = 2
            }

            R.id.signout -> {
                wantToChangeFragment = false
                signOutPressed = true
            }

            else -> {
                fragmentToSet = HomeFragment()
                nextFragment = 0
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    override fun onDrawerOpened(drawerView: View) {}

    override fun onDrawerClosed(drawerView: View) {
        if(wantToChangeFragment) {
            setFragment(fragmentToSet)
            wantToChangeFragment = false

        }else if(signOutPressed){

            this.createAlertDialog(
                "Confirm Sign Out",
                "${currentUserInfo?.name?:""}, you are signing out of BechDo on this device.",
                "Sign out",
                "Cancel"

            ) {
                auth.signOut()
                startActivity(
                    Intent(this, EmailActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }

            signOutPressed = false
        }
    }

    override fun onDrawerStateChanged(newState: Int) {}

    override fun onBackPressed() {
        when {

            drawer_layout.isDrawerOpen(GravityCompat.START) -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }

            supportFragmentManager.backStackEntryCount>0 ->{
                supportFragmentManager.popBackStack()
                if(!navDrawerBackStackIndices.isEmpty()) {
                    currentFragment = navDrawerBackStackIndices.pop()
                    nextFragment = currentFragment
                    navigation_view.menu.getItem(currentFragment).isChecked = true
                }
            }

            else -> super.onBackPressed()

        }
    }

    //Used to set Fragments which are also present as an option in the Navigation drawer
    fun changeFragmentFromDrawer(index:Int,fragment: Fragment){
        navigation_view.menu.getItem(index).isChecked = true
        nextFragment = index
        setFragment(fragment)
    }

    //Used to set Fragments which may or may not be present as an option in the Navigation drawer
    fun setFragment(fragment:Fragment){
        navDrawerBackStackIndices.push(currentFragment)
        currentFragment = nextFragment

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.slide_in,R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        if(fragment is HomeFragment){
            supportFragmentManager.popBackStackImmediate(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            navDrawerBackStackIndices.clear()
        }
    }

    fun setDp(){
        currentUserInfo?.downloadUrlDp.let {url->
            if(url!="") {
                Glide.with(this).load(url)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar).into(dp)
            }
        }
    }

    fun setName(){
        tvUserName.text = currentUserInfo?.name ?: ""
    }
}

fun Context.createAlertDialog(title:String,msg:String,positiveText:String,negativeText:String,positiveCallback:()->Unit){
    val tvTitle = TextView(this)

    tvTitle.apply {
        text = title
        setTextAppearance(android.R.style.TextAppearance_Material_Title)
        setTextColor(ContextCompat.getColor(this@createAlertDialog, R.color.light_black))
        setPadding(45, 30, 0, 0)
    }

    val dialog = MaterialAlertDialogBuilder(this)
        .setCustomTitle(tvTitle)
        .setMessage(msg)
        .setPositiveButton(positiveText) { _, _ ->
            positiveCallback.invoke()
        }
        .setNegativeButton(negativeText) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    val tvMessage = dialog.findViewById<TextView>(android.R.id.message)
    tvMessage?.apply {
        textSize = 15F
        setTextColor(
            ContextCompat.getColor(
                this@createAlertDialog,
                R.color.light_black
            )
        )
    }
}



