package com.yz3ro.chatcraft

import android.content.Context
import android.content.Intent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class menu {
    companion object {
        fun setupBottomNavigation(context: Context, bottomNav: BottomNavigationView) {
            bottomNav.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_messages -> {
                        // Mesajlar aktivitesini başlat
                        val intent = Intent(context, MesajlarActivity::class.java)
                        context.startActivity(intent)
                        true
                    }
                    R.id.navigation_contacts -> {
                        // Rehber aktivitesini başlat
                        val intent = Intent(context, RehberActivity::class.java)
                        context.startActivity(intent)
                        true
                    }
                    R.id.navigation_settings -> {
                        // Ayarlar aktivitesini başlat
                        val intent = Intent(context, AyarlarActivity::class.java)
                        context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }
    }
}
