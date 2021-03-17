package com.acash.bechdo.fragments.emailactivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.acash.bechdo.MainActivity
import com.acash.bechdo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpannableString()
        signInBtn.setOnClickListener {
            if (validateCredentials()) {
                signInUser(emailEt.text.toString(), pwdEt.text.toString())
            }
        }

        taglineBtn.setOnClickListener{
            if(tvTagline.visibility==View.GONE) {
                tvTagline.visibility = View.VISIBLE
                emailPwdLayout.visibility = View.GONE
                taglineBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_keyboard_arrow_up_24,null)
                )
            }else{
                tvTagline.visibility = View.GONE
                emailPwdLayout.visibility = View.VISIBLE
                taglineBtn.setImageDrawable(
                    ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_keyboard_arrow_down_24,null)
                )
            }
        }
    }

    private fun validateCredentials():Boolean {
        var isValid = true

        if (emailEt.text.isNullOrEmpty()) {
            emailEt.error = "Email cannot be empty!"
            isValid = false
        }

        if (pwdEt.text.isNullOrEmpty()) {
            pwdEt.error = "Password cannot be empty!"
            isValid = false
        }

        return isValid
    }

    private fun signInUser(email: String, pwd: String) {
        auth.signInWithEmailAndPassword(email,pwd)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    startActivity(Intent(requireContext(), MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }else{
                    Toast.makeText(requireContext(), task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun setSpannableString() {
        val span = SpannableString("New user ? Sign up")

        val clickableSpan = object : ClickableSpan() {
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
                Log.d("CLick", "clicked")
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, SignUpFragment())
                    ?.commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#E3386A")
            }
        }

        span.setSpan(
            clickableSpan,
            span.length - 7,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        tvSignUp.text = span
    }
}