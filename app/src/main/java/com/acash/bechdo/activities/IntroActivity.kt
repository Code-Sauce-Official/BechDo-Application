package com.acash.bechdo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acash.bechdo.utils.FadeOutTransformation
import com.acash.bechdo.R
import com.acash.bechdo.adapters.ViewPagerAdapter
import com.acash.bechdo.fragments.introactivity.IntroFragment1
import com.acash.bechdo.fragments.introactivity.IntroFragment2
import com.acash.bechdo.fragments.introactivity.IntroFragment3
import com.acash.bechdo.fragments.introactivity.IntroFragment4
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    private val fragments = arrayListOf(
            IntroFragment1(), IntroFragment2(), IntroFragment3(),
            IntroFragment4()
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewpager.adapter = ViewPagerAdapter(this,fragments)
        viewpager.setPageTransformer(FadeOutTransformation())
    }
}