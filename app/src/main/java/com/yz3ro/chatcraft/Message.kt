package com.yz3ro.chatcraft

import com.google.firebase.firestore.FieldValue

data class Message(
    val senderID: String = "",
    val receiverID: Long = 0,
    val text: String = "",
    val timestamp: String = FieldValue.serverTimestamp().toString()
)
