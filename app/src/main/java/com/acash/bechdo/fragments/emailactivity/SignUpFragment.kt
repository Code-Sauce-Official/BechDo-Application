package com.acash.bechdo.fragments.emailactivity

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.acash.bechdo.R
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
        }else if (pwdEt.text!!.length < 8) {
            pwdInput.error = "Password should be at-least 8 characters long"
            return false
        } else if (!pwdEt.text!!.any {
                it.isLetter()
            }) {
            pwdInput.error = "Password must contain at-least one letter"
            return false
        } else if (!pwdEt.text!!.any {
                it.isDigit()
            }) {
            pwdInput.error = "Password must contain at-least one digit"
            return false
        } else if (!pwdEt.text!!.any {
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
        }else if (!pwdEt.text.isNullOrEmpty() && (pwdEt.text.toString() != confirmPwdEt.text.toString())) {
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
                            showToast("We have sent a verification link on your e-mail address.")
                        }
                        ?.addOnFailureListener {
                            progressDialog.dismiss()
                            showToast(it.message.toString())
                        }
                }else{
                    progressDialog.dismiss()
                    showToast(task.exception?.message.toString())
                }
            }
    }

    private fun showToast(message:String){
        val toast = Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,0,-65)
        toast.show()
    }

    private fun setSpannableString(){
        val span = SpannableString("Already registered ? Sign in")

        val clickableSpan = object : ClickableSpan(){
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
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