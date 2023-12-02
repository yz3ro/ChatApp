package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val giris_buton = findViewById<Button>(R.id.giris_buton)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // Oturum açık ise, doğrudan ana ekranına yönlendir
            val intent = Intent(this, MesajlarActivity::class.java)
            startActivity(intent)
            finish()
        }
        giris_buton.setOnClickListener {
            val intent=Intent(this,AdActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)





        }

    }

}
