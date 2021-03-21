package com.acash.bechdo

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.acash.bechdo.adapters.ViewPagerAdapter
import com.acash.bechdo.fragments.introactivity.ClickPhotoFragment
import com.acash.bechdo.fragments.introactivity.CompareFragment
import com.acash.bechdo.fragments.introactivity.ContactFragment
import com.acash.bechdo.fragments.introactivity.ViewProductsFragment
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    private val fragments = arrayListOf(
            ViewProductsFragment(), ClickPhotoFragment(), CompareFragment(),
            ContactFragment()
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewpager.adapter = ViewPagerAdapter(this,fragments)
        viewpager.setPageTransformer(FadeOutTransformation())
    }
}