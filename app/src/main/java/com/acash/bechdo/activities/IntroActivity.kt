package com.acash.bechdo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acash.bechdo.utils.FadeOutTransformation
import com.acash.bechdo.R
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