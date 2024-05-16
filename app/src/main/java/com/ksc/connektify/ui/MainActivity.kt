package com.ksc.connektify.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.ksc.connektify.R
import com.ksc.connektify.adapter.ViewPagerAdapter
import com.ksc.connektify.databinding.ActivityMainBinding
import com.ksc.connektify.ui.fragment.CallFragment
import com.ksc.connektify.ui.fragment.ChatFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, NumberActivity::class.java))
            finish()
        }
        binding.btnSignOut.setOnClickListener {
            binding.dialogueBox.root.visibility = View.VISIBLE
            binding.dialogueBox.positiveButton.setOnClickListener {
                auth.signOut()
                startActivity(Intent(this, NumberActivity::class.java))
                finish()
            }
            binding.dialogueBox.negativeButton.setOnClickListener {
                binding.dialogueBox.root.visibility = View.GONE
            }
        }

        val fragmentArrayList = ArrayList<Fragment>()
        fragmentArrayList.add(ChatFragment())
        fragmentArrayList.add(CallFragment())

        val adapter = ViewPagerAdapter(this, supportFragmentManager, fragmentArrayList)
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
//        binding.tabs.getTabAt(0)!!.setIcon(R.drawable.tab_chat)
//        binding.tabs.getTabAt(1)!!.setIcon(R.drawable.tab_call)

    }
}

