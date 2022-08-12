package com.garee.oriens.views.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.garee.oriens.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait ...")
        progressDialog.setCanceledOnTouchOutside(false)

        login.setOnClickListener {
            validateData()
        }

        //Sign Up Span Clickable
        val text = "Don\'t Have an Account ? Register Here."
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }
        }

        spannableString.setSpan(clickableSpan1, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        noAccount.setText(spannableString, TextView.BufferType.SPANNABLE)
        noAccount.setTextColor(R.color.secondary)
        noAccount.movementMethod = LinkMovementMethod.getInstance()
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = email_sign_in.text.toString().trim()
        password = password_sign_in.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,
                "Invalid Email.",
                Toast.LENGTH_SHORT)
                .show()
        }else if (password.isEmpty()){
            Toast.makeText(this,
                "Please Enter your Password.",
                Toast.LENGTH_SHORT)
                .show()
        }else{
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Loggin in..")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Welcome Back.",
                    Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Login Failed. ${it.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }
}