package com.garee.oriens.views.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.garee.oriens.R
import com.garee.oriens.model.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_data.*
import java.util.*
import kotlin.collections.HashMap

class AddDataActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        result.setOnClickListener {
            calculateData()
        }

        btn_add.setOnClickListener {
            validateData()
        }

    }

    private fun calculateData() {
        val total : Float
        val total2 : Float
        value_ = _value.text.toString().trim()
        rate_ = _rate.text.toString().trim()
        split_ = _split.text.toString().trim()
        if (value_.isEmpty()){
            Toast.makeText(this,
            "Masukan Jumlah..", Toast.LENGTH_SHORT)
                .show()
        }else if (rate_.isEmpty()){
            Toast.makeText(this,
                "Masukan Rate..", Toast.LENGTH_SHORT)
                .show()
        }else if (rate_.isEmpty()){
            Toast.makeText(this,
                "Masukan Rate..", Toast.LENGTH_SHORT)
                .show()
        }else{
            total = value_.toFloat() * rate_.toFloat()
            total2 = total * split_.toFloat() / 100
            _total.text = total2.toString()
        }
    }

    private var name_ = ""
    private var value_ = ""
    private var rate_ = ""
    private var split_ = ""

    private fun validateData() {
        name_ = _name.text.toString().trim()
        value_ = _value.text.toString().trim()
        rate_ = _rate.text.toString().trim()
        split_ = _split.text.toString().trim()

        if (name_.isEmpty()){
            Toast.makeText(this,
                "Masukan Nama ..", Toast.LENGTH_SHORT)
                .show()
        }else if (value_.isEmpty()){
            Toast.makeText(this,
                "Masukan Jumlah..", Toast.LENGTH_SHORT)
                .show()
        }else if (rate_.isEmpty()){
            Toast.makeText(this,
                "Masukan Rate..", Toast.LENGTH_SHORT)
                .show()
        }else if (split_.isEmpty()){
            Toast.makeText(this,
                "Masukan Pembagian..", Toast.LENGTH_SHORT)
                .show()
        }else{
            addDatas()
        }
    }

    private fun addDatas() {
        progressDialog.setMessage("Sedang Menambahkan Data..")
        progressDialog.show()

        val uid = firebaseAuth.uid
        val timestamp = System.currentTimeMillis()
        val hashMap: HashMap<String, Any?> = HashMap()
        val formattedData = MyApplication.formatTimeStamp(timestamp.toLong())

        val total : Float
        val total2 : Float
        value_ = _value.text.toString().trim()
        rate_ = _rate.text.toString().trim()
        split_ = _split.text.toString().trim()
        total = value_.toFloat() * rate_.toFloat()
        total2 = total * split_.toFloat() / 100

        hashMap["id"] = "$timestamp"
        hashMap["date"] = formattedData
        hashMap["name"] = name_
        hashMap["value"] = value_.toInt()
        hashMap["rate"] = rate_.toInt()
        hashMap["split"] = split_.toInt()
        hashMap["total"] = total2.toInt()

        val ref = FirebaseDatabase.getInstance().getReference("Datas")
        ref.child(uid!!).child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Data Telah di tambahkan..",
                    Toast.LENGTH_SHORT)
                    .show()

                _name.text.clear()
                _value.text.clear()
                _rate.text.clear()
                 _split.text.clear()
                _total.text = "Result"

            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Gagal menambahkan Data ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
