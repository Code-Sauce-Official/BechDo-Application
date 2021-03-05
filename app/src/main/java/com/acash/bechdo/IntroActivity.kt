package com.acash.bechdo

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.acash.bechdo.adapters.ViewPagerAdapter
import com.acash.bechdo.fragments.ClickPhotoFragment
import com.acash.bechdo.fragments.SignUpFragment
import com.acash.bechdo.fragments.ViewProductsFragment
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class IntroActivity : AppCompatActivity() {
    private val fragments = arrayListOf(ClickPhotoFragment(), ViewProductsFragment(),
        SignUpFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }else window.setDecorFitsSystemWindows(false)

        viewpager.adapter = ViewPagerAdapter(this,fragments)
        viewpager.setPageTransformer(FadeOutTransformation())
        viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position==2){
                    emailEt.isEnabled = true
                    pwdEt.isEnabled = true
                }else if(emailEt!=null && pwdEt!=null){
                    emailEt.isEnabled = false
                    pwdEt.isEnabled = false
                }
            }
        })
    }
}