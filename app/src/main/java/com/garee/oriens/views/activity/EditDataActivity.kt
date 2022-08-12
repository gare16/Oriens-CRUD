package com.garee.oriens.views.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.garee.oriens.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_data.*
import kotlinx.android.synthetic.main.activity_edit_data.result

class EditDataActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var dataId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        dataId = intent.getStringExtra("dataId")!!

        edit_name.isEnabled = false
        loadDataDB()

        checkbox_name.setOnCheckedChangeListener{ buttonView, isChecked ->
            edit_name.isEnabled = isChecked

        }
        btn_edit.setOnClickListener {
            validateData()
        }
        result.setOnClickListener {
            calculateTotal()
        }
    }

    private fun calculateTotal() {
        val total : Float
        val total2 : Float
        value_ = edit_value.text.toString().trim()
        rate_ = edit_rate.text.toString().trim()
        split_ = edit_split.text.toString().trim()
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
            total_value.text = total2.toString()
        }
    }

    private var name_ = ""
    private var value_ = ""
    private var rate_ = ""
    private var split_ = ""
    private fun validateData() {
        name_ = edit_name.text.toString().trim()
        value_ = edit_value.text.toString().trim()
        rate_ = edit_rate.text.toString().trim()
        split_ = edit_split.text.toString().trim()

        if (value_.isEmpty()){
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
            updateDatas()
        }
    }


    private fun loadDataDB() {
        /**get Data*/
        val intent = intent
        val dataName = intent.getStringExtra("name")
        val dataRate = intent.getStringExtra("rate")
        val dataSplit = intent.getStringExtra("split")
        val dataValue = intent.getStringExtra("value")

        /**call text and images*/
        edit_name.setText("$dataName")
        edit_rate.setText("$dataRate")
        edit_split.setText("$dataSplit")
        edit_value.setText("$dataValue")
    }

    private fun updateDatas() {
        progressDialog.setMessage("Sedang Mengubah Data....")
        progressDialog.show()

        val total : Float
        val total2 : Float
        val hashmap = HashMap<String, Any>()
        total = value_.toFloat() * rate_.toFloat()
        total2 = total * split_.toFloat() / 100

        hashmap["name"] = "$name_"
        hashmap["rate"] = rate_.toInt()
        hashmap["split"] = split_.toInt()
        hashmap["value"] = value_.toInt()
        hashmap["total"] = total2.toInt()

        val ref = FirebaseDatabase.getInstance().getReference("Datas")
        ref.child(firebaseAuth.uid!!).child(dataId)
            .updateChildren(hashmap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                "Data Telah Diperbarui ..",Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Data Gagal Diperbarui ${e.message}",Toast.LENGTH_SHORT)
                    .show()
            }
    }
}