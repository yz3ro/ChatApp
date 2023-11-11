import com.google.firebase.firestore.FieldValue

data class Message(
    var senderId: String? = null,
    var receiverId: String? = null,
    var text: String? = null,
    var timestamp: Any? = null
)
