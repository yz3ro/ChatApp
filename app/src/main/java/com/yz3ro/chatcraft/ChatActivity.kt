package com.yz3ro.chatcraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    private val chatManager = ChatManager()
    private lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val receiverId = intent.getStringExtra("receiverUID") ?: ""
        val et_msg = findViewById<EditText>(R.id.et_msg)
        val btn_msg = findViewById<ImageView>(R.id.btn_msg)
        val messageRecyclerView: RecyclerView = findViewById(R.id.rec_chat)

        messageAdapter = MessageAdapter(emptyList())
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        chatManager.listenForMessages(currentUserUid, senderId, receiverId) { messages ->
            // Mesajları RecyclerView'da göster
            messageAdapter.messages = messages
            messageAdapter.notifyDataSetChanged()
            messageRecyclerView.scrollToPosition(messages.size - 1) // En son mesaja otomatik olarak kaydır
        }
        btn_msg.setOnClickListener {
            val Et_msg = et_msg.text.toString().trim()
            if (Et_msg.isNotEmpty()) {
                chatManager.sendMessage(senderId, receiverId, Et_msg)
                et_msg.text.clear()
            }
        }
    }
}