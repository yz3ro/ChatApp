package com.yz3ro.chatcraft

import com.google.firebase.firestore.FirebaseFirestore
private lateinit var mesajArrayList : ArrayList<Message>
private lateinit var receiverUserID : String
object FirestoreUtils {
    private val firestore = FirebaseFirestore.getInstance()
    private val messagesCollection = firestore.collection("messages")
    fun sendMessageToFirestore(messageText: String, senderUID: String, receiverUID: String) {
        val message = hashMapOf(
            "text" to messageText,
            "senderUID" to senderUID,
            "receiverUID" to receiverUID,
            "timestamp" to System.currentTimeMillis()
        )

        messagesCollection.add(message)
            .addOnSuccessListener { documentReference ->
                // Mesaj başarıyla gönderildi
            }
            .addOnFailureListener { e ->
                // Mesaj gönderme hatası
            }
    }

    fun getMessagesFromFirestore(
        senderUID: String,
        receiverUID: String,
        onMessagesReceived: (List<Message>) -> Unit
    ) {
        messagesCollection
            .whereEqualTo("senderUID", senderUID)
            .whereEqualTo("receiverUID", receiverUID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Hata
                } else {
                    mesajArrayList = arrayListOf()
                    for (document in value!!) {
                        val text = document.getString("text") ?: ""
                        val timestamp = document.getLong("timestamp") ?: 0
                        val senderUID = document.getString("senderUID") ?: ""
                        val receiverUID = document.getString("receiverUID") ?: ""

                        mesajArrayList.add(Message(text, timestamp, senderUID, receiverUID))
                    }
                    onMessagesReceived(mesajArrayList)
                }
            }
    }
}