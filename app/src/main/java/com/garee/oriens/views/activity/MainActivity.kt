package com.garee.oriens.views.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.garee.oriens.R
import com.garee.oriens.adapters.DataAdapter
import com.garee.oriens.adapters.SearchDataAdapter
import com.garee.oriens.databases.Database
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var mDataBase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataList: ArrayList<Database>
    private lateinit var searchList: ArrayList<Database>
    private lateinit var mAdapter: DataAdapter
    private lateinit var sAdapter: SearchDataAdapter
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()

        /**initialized*/
        dataList = arrayListOf<Database>()
        searchList = arrayListOf<Database>()
        recyclerViewData.layoutManager = LinearLayoutManager(this)
        recyclerViewData.setHasFixedSize(true)
        /**Get Data Firebase**/
        loadImageProfile()

        /** fab set **/
        fab_add.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }

        mainCIV.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        searchTV.setOnClickListener {
            mainCIV.visibility = View.GONE
            searchTV.visibility = View.GONE
            searchET.visibility = View.VISIBLE
            btnSearch.visibility = View.VISIBLE
        }

        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked){
                when(checkedId){
                    R.id.left ->
                        loadDatasRV()
                    R.id.right ->
                        Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                }
            }else {
                if (toggleButton.checkedButtonId == View.NO_ID){
                    dataList.clear()
                    Toast.makeText(this, "asikasik jos", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnSearch.setOnClickListener {
            searchByName()
        }
    }

    private var searchValue = ""
    private fun searchByName() {

        searchValue = searchTV.text.toString().trim()

        val ref = FirebaseDatabase.getInstance().getReference("Datas")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Checking if the value exists
                if (snapshot.exists()){
                    dataList.clear()
                    // looping through to values
                    for (i in snapshot.children){
                        val s = i.getValue(Database::class.java)
                        // checking if the name searched is available and adding to the array list
                        if (s!!.name == searchValue ){
                            dataList.add(s)
                        }
                    }
                    recyclerViewData.adapter = DataAdapter(applicationContext,dataList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,
                    "Data Error, ${error.message}", Toast.LENGTH_LONG)
                    .show()
            }

        })
    }

    private fun loadDatasRV() {
        mDataBase = FirebaseDatabase.getInstance().getReference("Datas")
        mDataBase.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (dataSnapshot in snapshot.children){
                        val i = dataSnapshot.getValue(Database::class.java)
                        dataList.add(i!!)
                    }
                    recyclerViewData.adapter = DataAdapter(applicationContext,dataList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,
                    error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun loadImageProfile() {
            val firebaseUser = firebaseAuth.currentUser!!
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val name = "${snapshot.child("name").value}"
                        val profileImage = "${snapshot.child("profileImage").value}"

                        usernameTV.text = "Hello, $name"

                        try {
                            Glide.with(this@MainActivity)
                                .load(profileImage)
                                .placeholder(R.drawable.img_broken)
                                .into(mainCIV)

                        }catch (e: Exception){

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
