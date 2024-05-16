package com.ksc.connektify.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ksc.connektify.adapter.MessageAdapter
import com.ksc.connektify.databinding.ActivityChatBinding
import com.ksc.connektify.model.MessageModel
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: FirebaseDatabase

    private lateinit var senderUID: String
    private var receiverUID: String? = ""
    private var receiverName: String? = ""
    private var receiverImage: String? = ""
    private var senderImage: String? = ""
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var messageList: ArrayList<MessageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        senderUID = FirebaseAuth.getInstance().uid.toString()
        receiverUID = intent.getStringExtra("uid")
        receiverName = intent.getStringExtra("name")
        receiverImage = intent.getStringExtra("image")

        messageList = ArrayList()
        senderRoom = senderUID + receiverUID
        receiverRoom = receiverUID + senderUID

        binding.tvChatUserName.text = receiverName
        Glide.with(this).load(receiverImage?.toUri()).into(binding.userImage)

        binding.fBtnSend.setOnClickListener {
            if (binding.etMessageBox.text.isEmpty()) {
                binding.etMessageBox.error = "Please enter your message"
            } else {
                val message =
                    MessageModel(binding.etMessageBox.text.toString(), senderUID, Date().time)
                val randomKey = database.reference.push().key

                database.reference.child("chats").child(senderRoom).child("message")
                    .child(randomKey!!).setValue(message).addOnSuccessListener {
                        database.reference.child("chats").child(receiverRoom).child("message")
                            .child(randomKey).setValue(message).addOnSuccessListener {
                                binding.etMessageBox.text.clear()
                            }
                    }
            }
        }

        database.reference.child("chats").child(senderRoom).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (snapshot1 in snapshot.children) {
                        val data = snapshot1.getValue(MessageModel::class.java)
                        messageList.add(data!!)
                    }
                    binding.recyclerViewChat.adapter =
                        MessageAdapter(this@ChatActivity, messageList, receiverImage)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}

