package com.garee.oriens.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.garee.oriens.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseAuth = FirebaseAuth.getInstance()

        loadProfileUser()

        editProfileBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    private fun loadProfileUser() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    val email = "${snapshot.child("email").value}"
                    val phone = "${snapshot.child("phone").value}"
                    val age = "${snapshot.child("age").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    profileName.text = name
                    profileEmail.text = email
                    profilePhone.text = "No Telepon : $phone"
                    profileAge.text = "Umur : $age"

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.img_broken)
                            .into(profileImg)

                    }catch (e: Exception){

                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}
