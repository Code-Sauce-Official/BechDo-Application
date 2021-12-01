package com.acash.bechdo.fragments.emailactivity

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.acash.bechdo.R
import com.acash.bechdo.activities.EmailActivity
import com.acash.bechdo.activities.createProgressDialog
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

        confirmPwdEt.addTextChangedListener {
            confirmPwdInput.isErrorEnabled = false
        }

        confirmPwdEt.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                checkConfirmPwd()
            }
        }
    }

    private fun validateCredentials():Boolean{
        var isValid = true

        if(!checkEmail()){
            isValid = false
        }

        if(!checkPassword()){
            isValid = false
        }

        if(!checkConfirmPwd()){
            isValid = false
        }

        return isValid
    }

    private fun checkEmail():Boolean{
        if(emailEt.text.isNullOrEmpty()){
            emailInput.error = "Email cannot be empty!"
            return false
        }
        return true
    }

    private fun checkPassword():Boolean{
        if(pwdEt.text.isNullOrEmpty()){
            pwdInput.error = "Password cannot be empty!"
            return false
        }

        if (pwdEt.text!!.length < 8) {
            pwdInput.error = "Password should be at-least 8 characters long"
            return false
        }

        if (!pwdEt.text!!.any {
                it.isLetter()
            }) {
            pwdInput.error = "Password must contain at-least one letter"
            return false
        }

        if (!pwdEt.text!!.any {
                it.isDigit()
            }) {
            pwdInput.error = "Password must contain at-least one digit"
            return false
        }

        if (!pwdEt.text!!.any {
                !it.isLetterOrDigit()
            }) {
            pwdInput.error = "Password must contain at-least one special character"
            return false
        }

        return true
    }

    private fun checkConfirmPwd():Boolean{
        if (confirmPwdEt.text.isNullOrEmpty()) {
            confirmPwdInput.error = "Required Field!"
            return false
        }

        if (!pwdEt.text.isNullOrEmpty() && (pwdEt.text.toString() != confirmPwdEt.text.toString())) {
            confirmPwdInput.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun signUpUser(email: String, pwd: String) {
        val progressDialog = requireContext().createProgressDialog("Signing up, Please wait...",false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email,pwd)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            progressDialog.dismiss()
                            (activity as EmailActivity).showToast("We have sent a verification link on your e-mail address.")
                        }
                        ?.addOnFailureListener {
                            progressDialog.dismiss()
                            (activity as EmailActivity).showToast(it.message.toString())
                        }
                }else{
                    progressDialog.dismiss()
                    (activity as EmailActivity).showToast(task.exception?.message.toString())
                }
            }
    }

    private fun setSpannableString(){
        val span = SpannableString("Already registered ? Sign in")

        val clickableSpan = object : ClickableSpan(){

            override fun onClick(widget: View) {
                (activity as EmailActivity).changeFragment(SignInFragment())
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