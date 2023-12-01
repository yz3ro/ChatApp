package com.yz3ro.chatcraft

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
class FirestoreHelper {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getLastMessageAndTime(userId: String, receiverId: String?, callback: (String?, Date?) -> Unit) {
        if (receiverId == null) {
            callback(null, null)
            return
        }

        firestore.collection("chats")
            .document("$userId-$receiverId")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val lastMessage = documentSnapshot.getString("lastMessage")
                val lastMessageTime = documentSnapshot.getDate("lastMessageTime")

                callback(lastMessage, lastMessageTime)
            }
            .addOnFailureListener { exception ->
                callback(null, null)
            }
    }
}