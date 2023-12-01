package com.yz3ro.chatcraft

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class mainmessageadapter(private val userList: List<User>) : RecyclerView.Adapter<mainmessageadapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mainmessageadapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_mainmessage,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: mainmessageadapter.MyViewHolder, position: Int) {
        val user : User = userList[position]
        holder.ad.text = user.ad
        holder.UserId.text = user.uid
        val lastMessage = user.lastMessage
        val lastMessageTime = user.lastMessageTime
        user.profilFotoURL?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .into(holder.img_main)
        }
        val firestoreHelper = FirestoreHelper()
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            firestoreHelper.getLastMessageAndTime(currentUserUid, user.uid) { lastMessage, lastMessageTime ->
                // lastMessage ve lastMessageTime değerlerini kullanarak işlemleri gerçekleştir
                // Örneğin, TextView'lara bu değerleri yerleştir
                holder.lastMessage.text = lastMessage ?: "Son mesaj bulunamadı"
                holder.lastMessageTime.text = formatTime(lastMessageTime)
            }
        }
    }
    private fun formatTime(lastMessageTime: Date?): String {
        // Burada lastMessageTime'ı istediğiniz formata dönüştürme işlemlerini yapabilirsiniz.
        // Örneğin, SimpleDateFormat kullanabilirsiniz.
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return lastMessageTime?.let {
            dateFormat.format(it)
        } ?: ""
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ad : TextView = itemView.findViewById(R.id.txt_adsoyad)
        val LayoutKisi : ConstraintLayout = itemView.findViewById(R.id.layout_kisi)
        val UserId : TextView = itemView.findViewById(R.id.textUid3)
        val lastMessage: TextView = itemView.findViewById(R.id.txt_lastmessage)
        val lastMessageTime: TextView = itemView.findViewById(R.id.txt_time)
        val img_main : CircleImageView = itemView.findViewById(R.id.img_main)

        init {
            LayoutKisi.setOnClickListener {
                val context = itemView.context
                val receiverUID = UserId.text.toString()
                val ad = ad.text.toString()
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("ad",ad)
                intent.putExtra("receiverUID",receiverUID)
                context.startActivity(intent)
            }

        }
    }

}
