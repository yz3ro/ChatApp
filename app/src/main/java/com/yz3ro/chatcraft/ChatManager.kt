package com.yz3ro.chatcraft


import Message
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatManager {
    private val firestore: FirebaseFirestore by lazy {
        Firebase.firestore
    }
    fun sendMessage(senderId: String, receiverId: String, text: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid
        if (userUID != null) {
            val message = Message(senderId, receiverId, text, FieldValue.serverTimestamp())
            firestore.collection("chats")
                .document("$senderId-$receiverId")
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    // Başarıyla eklendi
                }
                .addOnFailureListener { e ->
                    // Hata durumunda burası çalışır
                    Log.e("ChatManager", "Mesaj ekleme hatası: ${e.message}")
                }
        }
    }
    fun listenForMessages(
        currentUserUid: String,
        senderId: String,
        receiverId: String,
        listener: (List<Message>) -> Unit
    ) {
        firestore.collection("chats")
            .document("$senderId-$receiverId")
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ChatManager", "Mesajları dinleme hatası: ${exception.message}")
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()

                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    message?.let { messages.add(it) }
                }

                // Gönderilen ve alınan mesajları ayırt etmek istiyorsanız, burada filtreleme yapabilirsiniz
                val filteredMessages = messages.filter { it.senderId == senderId || it.senderId == receiverId }

                listener(filteredMessages)
            }
    }


}