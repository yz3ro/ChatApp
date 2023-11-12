package com.yz3ro.chatcraft


import Message
import android.util.Log
import com.google.firebase.Timestamp
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
                    Log.d("ChatManager", "sendMessage: Message sent successfully.")
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
        val chatId1 = "$senderId-$receiverId"
        val chatId2 = "$receiverId-$senderId"

        firestore.collection("chats")
            .document(chatId1)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot1, exception1 ->
                if (exception1 != null) {
                    Log.e("ChatManager", "Mesajları dinleme hatası: ${exception1.message}")
                    return@addSnapshotListener
                }

                firestore.collection("chats")
                    .document(chatId2)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot2, exception2 ->
                        if (exception2 != null) {
                            Log.e("ChatManager", "Mesajları dinleme hatası: ${exception2.message}")
                            return@addSnapshotListener
                        }

                        val messages = mutableListOf<Message>()

                        snapshot1?.documents?.forEach { document ->
                            val message = document.toObject(Message::class.java)
                            message?.let { messages.add(it) }
                        }

                        snapshot2?.documents?.forEach { document ->
                            val message = document.toObject(Message::class.java)
                            message?.let { messages.add(it) }
                        }

                        // Tüm mesajları timestamp'e göre sırala
                        messages.sortBy { message ->
                            val timestamp = when (val ts = message.timestamp) {
                                is Timestamp -> ts.toDate()
                                is Date -> ts
                                else -> null
                            }

                            // Eğer timestamp null değilse, sıralama işleminde kullan.
                            timestamp?.time ?: Long.MIN_VALUE
                        }



                        // Gönderen ve alıcıya göre filtreleme yap
                        val filteredMessages = messages.filter { (it.senderId == currentUserUid && it.receiverId == receiverId) || (it.senderId == receiverId && it.receiverId == currentUserUid) }

                        listener(filteredMessages)
                    }
            }
    }





}