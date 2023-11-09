package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

class AdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
       val imgbtn_ileri = findViewById<ImageButton>(R.id.imgbtn_ileri)//ileri sayfaya geç
        val txtKullaniciAdi = findViewById<TextView>(R.id.txt_ad)


    //ileri sayfaya geçtik
        imgbtn_ileri.setOnClickListener{
            val kullaniciAdi = txtKullaniciAdi.text.toString()
            val intent=Intent(this,NumActivity::class.java)
            intent.putExtra("kullaniciAdi", kullaniciAdi)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)



        }





    }
    fun onBackPressed(view: View) {
        val intent= Intent(this,MainActivity::class.java)

        startActivity(intent)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }
}