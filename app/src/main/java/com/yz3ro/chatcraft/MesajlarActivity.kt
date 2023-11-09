package com.yz3ro.chatcraft


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView



class MesajlarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setupBottomNavigation(this, bottomNav)
        bottomNav.menu.findItem(R.id.navigation_messages).setIcon(R.drawable.dolu_mesaj)
        bottomNav.menu.findItem(R.id.navigation_settings).setIcon(R.drawable.bos_ayarlar)
        bottomNav.menu.findItem(R.id.navigation_contacts).setIcon(R.drawable.bos_rehber)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)


    }
}
