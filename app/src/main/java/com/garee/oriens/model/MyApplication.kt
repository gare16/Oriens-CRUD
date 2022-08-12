package com.garee.oriens.model

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
    }
    companion object {
        fun formatTimeStamp(timestamp: Long): String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun deleteDataDB(context: Context, dataId: String){
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please Wait!")
            progressDialog.setMessage("Sedang Menghapus Data")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val firebaseAuth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Datas")
            ref.child(firebaseAuth.uid!!).child(dataId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context,
                    "Data Berhasil di Hapus", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener{e ->
                    progressDialog.dismiss()
                    Toast.makeText(context,
                        "Data Gagal di Hapus ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }

        }
    }
}