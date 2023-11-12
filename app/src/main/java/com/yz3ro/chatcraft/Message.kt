import com.google.firebase.Timestamp

data class Message(
    var senderId: String? = null,
    var receiverId: String? = null,
    var text: String? = null,
    var timestamp: Any? = null
)