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
import com.acash.bechdo.ProfileActivity
import com.acash.bechdo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpannableString()
        signUpBtn.setOnClickListener{
            if(validateCredentials()) {
                signUpUser(emailEt.text.toString(), pwdEt.text.toString())
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

    private fun validateCredentials():Boolean{
        var isValid = true

        if(emailEt.text.isNullOrEmpty()){
            emailEt.error = "Email cannot be empty!"
            isValid = false
        }

        if(pwdEt.text.isNullOrEmpty()){
            pwdEt.error = "Password cannot be empty!"
            isValid = false
        }

        if(confirmPwdEt.text.isNullOrEmpty()){
            confirmPwdEt.error = "Required Field!"
            isValid = false
        }

        if(!emailEt.text.isNullOrEmpty() && !pwdEt.text.isNullOrEmpty()) {
            if (pwdEt.text!!.length < 8) {
                pwdEt.error = "Password should be at-least 8 characters long"
                isValid = false
            } else if (!pwdEt.text!!.any {
                    it.isLetter()
                }) {
                pwdEt.error = "Password must contain at-least one letter"
                isValid = false
            } else if (!pwdEt.text!!.any {
                    it.isDigit()
                }) {
                pwdEt.error = "Password must contain at-least one digit"
                isValid = false
            } else if (!pwdEt.text!!.any {
                    !it.isLetterOrDigit()
                }) {
                pwdEt.error = "Password must contain at-least one special character"
                isValid = false
            } else if (!confirmPwdEt.text.isNullOrEmpty() && (pwdEt.text.toString() != confirmPwdEt.text.toString())) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                isValid = false
            }
        }

        return isValid
    }

    private fun signUpUser(email: String, pwd: String) {
        auth.createUserWithEmailAndPassword(email,pwd)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    startActivity(Intent(requireContext(),ProfileActivity::class.java)
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

    private fun setSpannableString(){
        val span = SpannableString("Already registered ? Sign in")

        val clickableSpan = object : ClickableSpan(){
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
                Log.d("CLick","clicked")
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, SignInFragment())
                    ?.commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#E3386A")
            }
        }

        span.setSpan(clickableSpan,span.length-7,span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSignIn.movementMethod = LinkMovementMethod.getInstance()
        tvSignIn.text = span
    }
}