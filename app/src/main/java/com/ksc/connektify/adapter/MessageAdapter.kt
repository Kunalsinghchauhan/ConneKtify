package com.ksc.connektify.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ksc.connektify.R
import com.ksc.connektify.databinding.ReceiverItemBinding
import com.ksc.connektify.databinding.SentItemLayoutBinding
import com.ksc.connektify.model.MessageModel
import com.ksc.connektify.model.UsersModel

class MessageAdapter(
    var context: Context,
    private var messageList: ArrayList<MessageModel>,
    private var userImage: String?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            SentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.sent_item_layout, parent, false)
            )
        } else {
            ReceivedViewHolder(
                LayoutInflater.from(context).inflate(R.layout.receiver_item, parent, false)
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (FirebaseAuth.getInstance().uid == messageList[position].sender) ITEM_SENT else ITEM_RECEIVE
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder.itemViewType == ITEM_SENT) {
            val viewHolder = holder as SentViewHolder
            viewHolder.binding.userMessage.text = message.message
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            val usersRef = FirebaseDatabase.getInstance().getReference("users")

            usersRef.child(currentUserUid!!).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UsersModel::class.java)
                    val photoUrl = user?.imageURL
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(context).load(photoUrl).into(viewHolder.binding.senderProfile)
                    } else {
                        // If user photo is not available, you can load a default placeholder or do nothing
                        // For example, Glide.with(context).load(R.drawable.default_user_photo).into(viewHolder.binding.senderProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            } else {
            val viewHolder = holder as ReceivedViewHolder
            viewHolder.binding.receiverMessage.text = message.message
            Glide.with(context).load(userImage?.toUri()).into(viewHolder.binding.receiverProfile)
        }
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SentItemLayoutBinding = SentItemLayoutBinding.bind(itemView)

    }

    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ReceiverItemBinding = ReceiverItemBinding.bind(itemView)

    }
}
