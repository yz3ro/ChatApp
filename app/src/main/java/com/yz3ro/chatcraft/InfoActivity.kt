package com.yz3ro.chatcraft

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InfoActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        val info_back = findViewById<ImageView>(R.id.info_back)
        val info_name = findViewById<TextView>(R.id.info_name)
        val info_num = findViewById<TextView>(R.id.info_num)
        val info_del = findViewById<TextView>(R.id.info_del)
        val info_pp = findViewById<ImageView>(R.id.info_foto)
        info_back.setOnClickListener { onBackPressed() }
        val rec = intent.getStringExtra("rec")

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid
        if (userUID != null) {

            db.collection("kullanicilar").document(userUID).collection("kisiler")
                .whereEqualTo("uid",rec)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val ad = document.getString("ad")
                        val telefon = document.getString("telefon")
                        info_name.text = ad.toString()
                        info_num.text = telefon.toString()
                    }

                }
                .addOnFailureListener{
                    Log.e("Hata", "Veri çekme hatası:")
                }
        }
        info_del.setOnClickListener {
            if (userUID != null) {
                val query = db.collection("kullanicilar").document(userUID).collection("kisiler")
                    .whereEqualTo("uid", rec)

                query.get()
                    .addOnSuccessListener { documents ->
                        // Sorgu sonuçlarını kontrol et
                        if (documents.isEmpty) {
                            // Belge bulunamadı
                            Log.d("Firebase", "Belge bulunamadı.")
                        } else {
                            // Belge bulundu, şimdi silelim
                            for (document in documents) {
                                db.collection("kullanicilar").document(userUID).collection("kisiler")
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Belge başarıyla silindi.")
                                        intent = Intent(this,RehberActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("Firebase", "Belge silme hatası: ${exception.message}")
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Sorgu başarısız olduğunda burası çalışır
                        Log.e("Firebase", "Sorgu başarısız: ${exception.message}")
                    }
            }
        }

    }
}