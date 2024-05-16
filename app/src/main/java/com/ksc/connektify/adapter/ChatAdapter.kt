package com.ksc.connektify.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ksc.connektify.R
import com.ksc.connektify.databinding.ChatUserItemLayoutBinding
import com.ksc.connektify.model.UsersModel
import com.ksc.connektify.ui.ChatActivity

class ChatAdapter(var context: Context, private var list: ArrayList<UsersModel>) :

    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ChatUserItemLayoutBinding = ChatUserItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_user_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val user = list[position]
        Glide.with(context).load(user.imageURL).into(holder.binding.imgUserProfile)
        holder.binding.tvUserName.text = user.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("uid", user.uID)
            intent.putExtra("name", user.name)
            intent.putExtra("image", user.imageURL)

            Log.i("kunalSinghChauhan", "onBindViewHolder: Receiver User ID is ${user.uID}")
            context.startActivity(intent)
        }
    }
}