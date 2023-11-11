package com.yz3ro.chatcraft

import Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(var messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val recMessageTextView : TextView = itemView.findViewById(R.id.messageTextView1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (message.senderId == currentUserUid) {
            holder.messageTextView.text = message.text
            holder.recMessageTextView.visibility = View.GONE
        } else {
            // Mesajı alıcı kişi ise receiverMessageTextView'a yerleştir
            holder.recMessageTextView.text = message.text
            holder.messageTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
