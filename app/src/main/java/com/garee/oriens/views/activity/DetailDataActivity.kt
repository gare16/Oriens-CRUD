package com.garee.oriens.views.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.garee.oriens.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_detail_data.*

class DetailDataActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var dataId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_data)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        dataId = intent.getStringExtra("dataId")!!


        /**get Data*/
        val intent = intent
        val dataName = intent.getStringExtra("name")
        val dataDate = intent.getStringExtra("date")
        val dataRate = intent.getStringExtra("rate")
        val dataSplit = intent.getStringExtra("split")
        val dataTotal = intent.getStringExtra("total")
        val dataValue = intent.getStringExtra("value")

        /**call text and images*/
        name.text = dataName
        date.text = dataDate
        rate.text = "Rate : $dataRate"
        split.text = "Pembagian : $dataSplit %"
        total.text = "Total : $dataTotal"
        value.text = "Jumlah : $dataValue"

        btnDelete.setOnClickListener {
            openDialogs()
        }
    }
    private fun openDialogs() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Delete Data")
            .setMessage("Apa kamu yakin ingin menghapus data ini ?")
            .setNegativeButton("Batal") { dialog, which ->
                Toast.makeText(this, "Cancelled..", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Ya!") { dialog, which ->
                deleteCurrentData()
            }
            .show()
    }

    private fun deleteCurrentData() {
        progressDialog.setMessage("Sedang Menghapus Data")
        progressDialog.show()

        val firebaseAuth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("Datas")
        ref.child(firebaseAuth.uid!!).child(dataId)
            .removeValue()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Data Berhasil di Hapus", Toast.LENGTH_SHORT)
                    .show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Data Gagal di Hapus ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}