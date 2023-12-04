package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

class RehberActivity : AppCompatActivity() {
    private lateinit var userArrayList : ArrayList<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: rehber_adapter
    private lateinit var db : FirebaseFirestore
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rehber)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setupBottomNavigation(this,bottomNav)
        bottomNav.menu.findItem(R.id.navigation_contacts).setIcon(R.drawable.dolu_rehber)
        bottomNav.menu.findItem(R.id.navigation_messages).setIcon(R.drawable.bos_mesaj)
        bottomNav.menu.findItem(R.id.navigation_settings).setIcon(R.drawable.bos_ayarlar)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        val kisi_ekle = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        kisi_ekle.setOnClickListener {
            intent = Intent(this,RehberEkleActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        myAdapter = rehber_adapter(userArrayList)
        recyclerView.adapter = myAdapter
        EventChangeListener()
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