package com.yz3ro.chatcraft

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class rehber_adapter(private val userList: List<User>) : RecyclerView.Adapter<rehber_adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rehber_adapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rehber_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: rehber_adapter.MyViewHolder, position: Int) {
        val user : User = userList[position]
        holder.ad.text = user.ad
        holder.telefon.text = user.telefon
        holder.UserId.text = user.uid
    }

    override fun getItemCount(): Int {
       return userList.size
    }
    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ad : TextView = itemView.findViewById(R.id.textViewKisiAdi)
        val telefon : TextView = itemView.findViewById(R.id.textViewTelefon)
        val LayoutKisi : LinearLayout = itemView.findViewById(R.id.LayoutKisi)
        val UserId : TextView = itemView.findViewById(R.id.textUid)

        init {
            LayoutKisi.setOnClickListener {
                val context = itemView.context
                val receiverUID = UserId.text.toString()
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("receiverUID",receiverUID)
                context.startActivity(intent)
            }

        }
        }

    }
