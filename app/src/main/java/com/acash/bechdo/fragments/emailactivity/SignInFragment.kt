package com.acash.bechdo.fragments.emailactivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
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
        setSpannableStrings()
        signInBtn.setOnClickListener {
            if(validateCredentials()){
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

        emailEt.addTextChangedListener {
            emailInput.isErrorEnabled = false
        }

        emailEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkEmail()
            }
        }

        pwdEt.addTextChangedListener {
            pwdInput.isErrorEnabled = false
        }

        pwdEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                checkPassword()
            }
        }
    }

    private fun validateCredentials():Boolean {
        var isValid = true

        if(!checkEmail()){
            isValid = false
        }

        if(!checkPassword()){
            isValid = false
        }

        return isValid
    }

    private fun checkEmail(): Boolean {
        if (emailEt.text.isNullOrEmpty()) {
            emailInput.error = "Email cannot be empty!"
            return false
        }
        return true
    }

    private fun checkPassword():Boolean {
        if (pwdEt.text.isNullOrEmpty()) {
            pwdInput.error = "Password cannot be empty!"
            return false
        }
        return true
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

    private fun setSpannableStrings() {
        //New user ? Sign up
        val spanNewUser = SpannableString("New user ? Sign up")

        val clickableSpanNewUser = object : ClickableSpan() {
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

        spanNewUser.setSpan(
            clickableSpanNewUser,
            spanNewUser.length - 7,
            spanNewUser.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        tvSignUp.text = spanNewUser

        //Forgot Password?
        val spanForgotPwd = SpannableString("Forgot Password ?")
        val clickableSpanForgotPwd = object : ClickableSpan() {
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
                if(emailEt.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(),"Enter your registered Email id",Toast.LENGTH_SHORT).show()
                }else {
                    auth.sendPasswordResetEmail(emailEt.text.toString())
                        .addOnCompleteListener{task->
                            if(task.isSuccessful){
                                Toast.makeText(requireContext(),"We have sent you instructions to reset password on your mail",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(requireContext(),"Failed to send reset mail",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#312E5F")
            }
        }

        spanForgotPwd.setSpan(
            clickableSpanForgotPwd,
            0,
            spanForgotPwd.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvForgotPwd.movementMethod = LinkMovementMethod.getInstance()
        tvForgotPwd.text = spanForgotPwd
    }
}