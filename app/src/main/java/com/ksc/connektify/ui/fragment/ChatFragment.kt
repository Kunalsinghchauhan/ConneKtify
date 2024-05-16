package com.ksc.connektify.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ksc.connektify.adapter.ChatAdapter
import com.ksc.connektify.databinding.FragmentChatBinding
import com.ksc.connektify.model.UsersModel

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userList: ArrayList<UsersModel>
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userList = ArrayList()
        progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE


        database.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressBar.visibility = View.GONE
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(UsersModel::class.java)
                        if (currentUser?.uID != auth.currentUser?.uid) {
                            userList.add(currentUser!!)
                        }
                    }
                    binding.recyclerViewUserList.adapter = ChatAdapter(requireContext(), userList)
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            }
            )
        return binding.root
    }
}