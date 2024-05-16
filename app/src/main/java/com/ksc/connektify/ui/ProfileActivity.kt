package com.ksc.connektify.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ksc.connektify.databinding.ActivityProfileBinding
import com.ksc.connektify.model.UsersModel
import java.util.Date

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImage: Uri
    private lateinit var dialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = AlertDialog.Builder(this).setTitle("Logging In").setMessage("Please Wait...")
            .setCancelable(false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.imgProfile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.btnSave.setOnClickListener {
            if (binding.etName.text.toString().isEmpty()) {
                Snackbar.make(binding.root, "Name is required", Snackbar.LENGTH_SHORT).show()
            } else if (!::selectedImage.isInitialized) {
                // Toast if image is not selected
                Snackbar.make(binding.root, "Please select an image", Snackbar.LENGTH_SHORT).show()
            } else {
                dialog.show()
                uploadData()
            }
        }
    }

    private fun uploadData() {
        val reference = storage.reference.child("Profiles").child(Date().time.toString())
        reference.putFile(selectedImage).addOnCompleteListener {
            if (it.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { task ->
                    uploadInfo(task.toString())
                }
            }
        }
    }

    private fun uploadInfo(imgUrl: String) {
        val user = UsersModel(
            auth.uid.toString(),
            binding.etName.text.toString(),
            auth.currentUser!!.phoneNumber.toString(),
            imgUrl
        )
        //add to firebase
        database.reference.child("users")
            .child(auth.uid.toString())
            .setValue(user)
            .addOnSuccessListener {
                Snackbar.make(binding.root, "Profile Updated", Snackbar.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                selectedImage = data.data!!
                binding.imgProfile.setImageURI(selectedImage)
            }
        }
    }
}