package com.ksc.connektify.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ksc.connektify.R
import com.ksc.connektify.databinding.ActivityNumberBinding

class NumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNumberBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (binding.etPhoneNumber.text.toString().isEmpty()) {
                Snackbar.make(this, binding.root, "Please enter a phone number", Snackbar.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this,OTPActivity::class.java)
                intent.putExtra("phoneNumber", binding.etPhoneNumber.text.toString())
                startActivity(intent)
            }
        }
    }
}