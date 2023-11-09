package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ChatActivity : AppCompatActivity() {
    private lateinit var et_mesaj : EditText
    private lateinit var mesaj_gonder : ImageView
    private lateinit var senderUserID : String
    private lateinit var receiverUserID : String
    private lateinit var mesajArrayList : ArrayList<Message>
    private lateinit var mesajAdapter : MesajAdapter
    private var db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("mesajlar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        receiverUserID = intent.getStringExtra("receiverUID") ?: ""
        et_mesaj = findViewById(R.id.et_mesaj)
        mesaj_gonder= findViewById(R.id.mesaj_gonder)
        senderUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val recyclerView = findViewById<RecyclerView>(R.id.rec_chat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        mesajArrayList = arrayListOf()
        mesajAdapter = MesajAdapter(mesajArrayList)
        recyclerView.adapter = mesajAdapter
        mesaj_gonder.setOnClickListener {
            intent = Intent(this,FirestoreUtils::class.java)
            intent.putExtra("receiverUID",receiverUserID)
            startActivity(intent)
            val mesaj = et_mesaj.text.toString().trim()
            if (mesaj.isNotBlank()){
                senderUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                receiverUserID = intent.getStringExtra("receiverUID") ?: ""
                FirestoreUtils.sendMessageToFirestore(mesaj, senderUserID, receiverUserID)
                et_mesaj.text.clear()
            }
        }
        FirestoreUtils.getMessagesFromFirestore(senderUserID, receiverUserID) { messages ->
            mesajAdapter.setMessages(messages)
            recyclerView.scrollToPosition(messages.size - 1)
    }
}