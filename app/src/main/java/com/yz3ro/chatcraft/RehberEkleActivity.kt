package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.play.integrity.internal.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hbb20.CountryCodePicker


class RehberEkleActivity : AppCompatActivity() {
    private var tam_num = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rehber_ekle)
        val btn_Onayla = findViewById<Button>(R.id.btnOnayla)
        val tel = findViewById<EditText>(R.id.editTextTelefon)
        val ad = findViewById<EditText>(R.id.editTextKisiAdi)
        val imgb_geri = findViewById<ImageButton>(R.id.imgb_geri)

        imgb_geri.setOnClickListener { intent = Intent(this,RehberActivity::class.java);startActivity(intent)  }
        btn_Onayla.setOnClickListener {
            val kisiadi = ad.text.toString()
            val numara = tel.text.toString()
            val db = FirebaseFirestore.getInstance()
            val countryCodePicker = findViewById<CountryCodePicker>(R.id.countyCodePicker)
            val countryCode = countryCodePicker.selectedCountryCodeWithPlus
            tam_num = "$countryCode$numara"
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(currentUser != null){
                db.collection("kullanicilar")
                    .whereEqualTo("numara",tam_num)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty){
                            Log.d("Hata", "Bu numara ile eşleşen kullanıcı bulunamadı.")
                        }else {
                            for (document in documents) {
                                val uid = document.getString("uid")
                                if (uid != null) {
                                    val kisi = hashMapOf(
                                        "ad" to kisiadi,
                                        "telefon" to tam_num,
                                        "uid" to uid
                                    )
                                    db.collection("kullanicilar")
                                        .document(currentUser.uid)
                                        .collection("kisiler")
                                        .add(kisi)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d("dönüs", "Veri eklendi. ID: ${documentReference.id}")
                                        }
                                        .addOnFailureListener { documentReference ->
                                            Log.e("dönüs", "Veri eklenirken hata oluştu:")
                                        }

                                } else {
                                    Log.e("Hata", "Kullanıcı oturumu açık değil veya hata oluştu.")
                                }
                            }
                        }
                        }
                    .addOnFailureListener {e->
                        Log.e("Hata", "Kişi aranırken hata oluştu: ${e.message}")
                    }
                    }


            intent = Intent(this,RehberActivity::class.java); startActivity(intent)
            }
        }
    }