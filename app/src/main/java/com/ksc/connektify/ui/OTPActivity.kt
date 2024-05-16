package com.ksc.connektify.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ksc.connektify.R
import com.ksc.connektify.databinding.ActivityOtpBinding
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationID: String
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        dialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_dialog_box)
            .setMessage("\nPlease Wait...")
            .setTitle("LOADING")
            .setCancelable(false)
            .create()
        dialog.show()
        val phoneNumber = "+91" + intent.getStringExtra("phoneNumber")

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Snackbar.make(
                        this@OTPActivity,
                        binding.root,
                        "Verification Completed",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dialog.dismiss()
                    Snackbar.make(
                        this@OTPActivity,
                        binding.root,
                        "Verification FAILED",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Snackbar.make(this@OTPActivity, binding.root, "OTP SENT", Snackbar.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                    verificationID = p0

                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.btnNext.setOnClickListener {
            if (binding.etPhoneOtp.text!!.isEmpty()) {
                Snackbar.make(
                    this@OTPActivity,
                    binding.root,
                    "Please Enter OTP",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(
                    verificationID,
                    binding.etPhoneOtp.text.toString()
                )
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Check if user exists in the database
                        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                        userRef.child(currentUserID!!).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    startActivity(Intent(this@OTPActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    startActivity(Intent(this@OTPActivity, ProfileActivity::class.java))
                                    finish()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Handle onCancelled
                            }
                        })
                    } else {
                        dialog.dismiss()
                        Snackbar.make(
                            this@OTPActivity,
                            binding.root,
                            "Login Failed",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}