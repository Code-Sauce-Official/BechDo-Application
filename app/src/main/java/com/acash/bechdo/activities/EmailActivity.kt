package com.acash.bechdo.activities

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.acash.bechdo.R
import kotlinx.android.synthetic.main.activity_email.*

class EmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        taglineBtn.setOnClickListener{
            if(tvTagline.visibility== View.GONE) {
                tvTagline.visibility = View.VISIBLE
                fragment_container.visibility = View.GONE
                taglineBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_keyboard_arrow_up_24,null)
                )
            }else{
                tvTagline.visibility = View.GONE
                fragment_container.visibility = View.VISIBLE
                taglineBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_keyboard_arrow_down_24,null)
                )
            }
        }
    }

    fun showToast(message:String){
        val toast = Toast.makeText(this,message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,0,-65)
        toast.show()
    }

    fun changeFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            .replace(R.id.fragment_container,fragment)
            .commit()
    }
}