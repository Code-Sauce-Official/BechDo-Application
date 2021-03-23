package com.acash.bechdo.fragments.emailactivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
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
import com.acash.bechdo.MainActivity
import com.acash.bechdo.ProfileActivity
import com.acash.bechdo.R
import com.acash.bechdo.createProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
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
        val progressDialog = requireContext().createProgressDialog("Signing in, Please wait...",false)
        progressDialog.show()
        auth.signInWithEmailAndPassword(email,pwd)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    if(auth.currentUser?.isEmailVerified == true) {
                        database.collection("users").document(auth.uid.toString()).get()
                            .addOnSuccessListener {
                                if (it.exists()) {
                                    startActivity(
                                        Intent(requireContext(), MainActivity::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                } else {
                                    startActivity(
                                        Intent(requireContext(), ProfileActivity::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                showToast(it.message.toString())
                            }
                    }else{
                        progressDialog.dismiss()
                        showToast("Please verify your e-mail address!")
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

    private fun setSpannableStrings() {
        //New user ? Sign up
        val spanNewUser = SpannableString("New user ? Sign up")

        val clickableSpanNewUser = object : ClickableSpan() {
            override fun onClick(widget: View) {
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
                    showToast("Enter your registered Email id")
                }else {
                    auth.sendPasswordResetEmail(emailEt.text.toString())
                        .addOnCompleteListener{task->
                            if(task.isSuccessful){
                                showToast("We have sent you instructions to reset password on your mail")
                            }else{
                                showToast(task.exception?.message.toString())
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

        //Send Verification mail
        val spanVerification = SpannableString("Send Verification mail")

        val clickableSpanVerification = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(validateCredentials()) {
                    val countDownTimer = object : CountDownTimer(30000, 1000) {

                        override fun onTick(millisUntilFinished: Long) {
                            tvSendVerificationMail.text =
                                getString(R.string.countdown,millisUntilFinished/1000)
                        }

                        override fun onFinish() {
                            tvSendVerificationMail.text = spanVerification
                        }
                    }.start()

                    auth.signInWithEmailAndPassword(emailEt.text.toString(), pwdEt.text.toString())
                        .addOnSuccessListener {
                            if (auth.currentUser?.isEmailVerified == true) {
                                countDownTimer.cancel()
                                tvSendVerificationMail.text = spanVerification
                                showToast("Email Address has already been verified")
                            } else {
                                auth.currentUser?.sendEmailVerification()
                                    ?.addOnSuccessListener {
                                        showToast("We have sent a verification link on your e-mail address.")
                                    }
                                    ?.addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                            }
                        }
                        .addOnFailureListener {
                            countDownTimer.cancel()
                            tvSendVerificationMail.text = spanVerification
                            showToast(it.message.toString())
                        }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#312E5F")
            }
        }

        spanVerification.setSpan(
            clickableSpanVerification,
            0,
            spanVerification.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvSendVerificationMail.movementMethod = LinkMovementMethod.getInstance()
        tvSendVerificationMail.text = spanVerification
    }
}