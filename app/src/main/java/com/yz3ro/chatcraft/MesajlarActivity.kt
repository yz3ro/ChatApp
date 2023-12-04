package com.yz3ro.chatcraft


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class MesajlarActivity : AppCompatActivity() {
    private lateinit var userArrayList : ArrayList<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: mainmessageadapter
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setupBottomNavigation(this, bottomNav)
        bottomNav.menu.findItem(R.id.navigation_messages).setIcon(R.drawable.dolu_mesaj)
        bottomNav.menu.findItem(R.id.navigation_settings).setIcon(R.drawable.bos_ayarlar)
        bottomNav.menu.findItem(R.id.navigation_contacts).setIcon(R.drawable.bos_rehber)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        recyclerView = findViewById(R.id.rcy_mainmessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        myAdapter = mainmessageadapter(userArrayList)
        recyclerView.adapter = myAdapter
        EventChangeListener()
        val rec = intent.getStringExtra("rec")
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid

    }


    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid
        if (userUID != null) {
            db.collection("kullanicilar").document(userUID).collection("kisiler").
            addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if(error != null){
                        Log.e("firestore error",error.message.toString())
                        return
                    }
                    for (dc : DocumentChange in value?.documentChanges!!){

                        if (dc.type==DocumentChange.Type.ADDED){
                            userArrayList.add(dc.document.toObject(User::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }

            })

        }
    }

}
