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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait ...")
        progressDialog.setCanceledOnTouchOutside(false)

        btn_register.setOnClickListener {
            validateData()
        }

        //Span TV to LoginActivity
        val text = "Already have Account? Click Here."
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            }
        }

        spannableString.setSpan(clickableSpan1, 22, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        haveAccount.setText(spannableString, TextView.BufferType.SPANNABLE)
        haveAccount.setTextColor(R.color.secondary)
        haveAccount.movementMethod = LinkMovementMethod.getInstance()
    }

    private var name = ""
    private var email = ""
    private var password = ""
    private var cPassword = ""
    private fun validateData() {
        name = name_regis.text.toString().trim()
        email = email_regis.text.toString().trim()
        password = password_regis.text.toString().trim()
        cPassword = cpassword_regis.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,
                "Please Enter your Name.",
                Toast.LENGTH_SHORT)
                .show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,
                "Invalid Email.",
                Toast.LENGTH_SHORT)
                .show()
        }else if(password.isEmpty()){
            Toast.makeText(this,
                "Please Enter your Password.",
                Toast.LENGTH_SHORT)
                .show()
        }else if (cPassword.isEmpty()){
            Toast.makeText(this,
            "Please Enter your Confirm Password",
            Toast.LENGTH_SHORT)
                .show()
        }else if(password != cPassword){
            Toast.makeText(this,
                "Password Does not Match!",
                Toast.LENGTH_SHORT)
                .show()
        }else{
            createAccount()
        }
    }

    private fun createAccount() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener {
                Toast.makeText(this,
                    "Failed create account.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Sedang Menambahkan ...")
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap : HashMap<String, Any?> = HashMap()

        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["phone"] = "".toInt()
        hashMap["age"] = "".toInt()
        hashMap["profileImage"] = ""
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Account Created..",
                    Toast.LENGTH_SHORT)
                    .show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Failed saving user info ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}