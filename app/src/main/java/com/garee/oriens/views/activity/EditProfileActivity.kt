
package com.garee.oriens.views.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.garee.oriens.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imgUrl: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        btnEditProfile.setOnClickListener {
            validateData()
        }
        editImgProfile.setOnClickListener {
            onAttachImages()
        }
    }

    private fun onAttachImages() {
        val popupMenu = PopupMenu(this, editImgProfile)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0){
                pickImageCamera()
            }
            else if (id == 1 ){
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imgUrl = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl)
        cameraActivityResultLauncher.launch(intent)
    }
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                imgUrl = data!!.data

                editImgProfile.setImageURI(imgUrl)
            }else{
                Toast.makeText(this,
                    "Cancelled",Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imgUrl = data!!.data

                editImgProfile.setImageURI(imgUrl)
            }else{
                Toast.makeText(this,
                    "Cancelled",Toast.LENGTH_SHORT)
                    .show()
            }

        }
    )

    private var name = ""
    private var phone = ""
    private var age = ""

    private fun validateData() {
        progressDialog.setMessage("Sedang Mengubah Data..")
        progressDialog.show()
        name = nameEditProfile.text.toString().trim()
        phone = phoneEditProfile.text.toString().trim()
        age = ageEditProfile.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,
                "Masukan Nama ..", Toast.LENGTH_SHORT)
                .show()
        }else{
            if (imgUrl == null){
                editProfile("")
            }else{
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading Image..")
        progressDialog.show()

        val filePathAndName = "profileImage/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imgUrl!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                editProfile(uploadedImageUrl)

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Gagal Upload Gambar ${e.message}",Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun editProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Uploading New Profile..")
        val hashMap: HashMap<String,Any > = HashMap()
        hashMap["name"] = "${name}"
        hashMap["phone"] = phone.toInt()
        hashMap["age"] = age.toInt()

        if (imgUrl !== null){
            hashMap["profileImage"] = uploadedImageUrl
        }

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Profile Terlah diperbarui..",
                    Toast.LENGTH_SHORT)
                    .show()

                nameEditProfile.text.clear()
                phoneEditProfile.text.clear()
                ageEditProfile.text.clear()
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Gagal Memperbarui Profile ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }
}