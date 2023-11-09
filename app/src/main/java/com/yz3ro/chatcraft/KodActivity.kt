package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class KodActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var kod: String = ""
    private lateinit var editText1 : EditText
    private lateinit var editText2 : EditText
    private lateinit var editText3 : EditText
    private lateinit var editText4 : EditText
    private lateinit var editText5 : EditText
    private lateinit var editText6 : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kod)
        editText1 = findViewById<EditText>(R.id.editText1)
        editText2 = findViewById<EditText>(R.id.editText2)
        editText3 = findViewById<EditText>(R.id.editText3)
        editText4 = findViewById<EditText>(R.id.editText4)
        editText5 = findViewById<EditText>(R.id.editText5)
        editText6 = findViewById<EditText>(R.id.editText6)
        editText1.addTextChangedListener(textWatcher)
        editText2.addTextChangedListener(textWatcher)
        editText3.addTextChangedListener(textWatcher)
        editText4.addTextChangedListener(textWatcher)
        editText5.addTextChangedListener(textWatcher)
        auth = FirebaseAuth.getInstance()
        val verificationId = intent.getStringExtra("verificationId") ?: ""
        val imgbuton_ileri = findViewById<ImageButton>(R.id.imgbtn_ileri)
        imgbuton_ileri.setOnClickListener {
            Log.d("OlayTakibi", "tıklandı.")
            val sayi1 = editText1.text.toString()
            val sayi2 = editText2.text.toString()
            val sayi3 = editText3.text.toString()
            val sayi4 = editText4.text.toString()
            val sayi5 = editText5.text.toString()
            val sayi6 = editText6.text.toString()
            kod = sayi1 + sayi2 + sayi3 + sayi4 + sayi5 + sayi6
            val verificationCode = kod
            verifyVerificationCode(verificationCode, verificationId)
            val kullaniciAdi = intent.getStringExtra("kullaniciAdi") ?: "Varsayılan Kullanıcı Adı"
            val currentUser = auth.currentUser
            val db = FirebaseFirestore.getInstance()
            if (currentUser != null) {
                Log.d("OlayTakibi", "içeri girildi.")
                val kullanicilar = hashMapOf(
                    "uid" to currentUser.uid,
                    "numara" to currentUser.phoneNumber,
                    "ad" to kullaniciAdi,
                )
                db.collection("kullanicilar")
                    .document(currentUser.uid)
                    .set(kullanicilar)
                    .addOnSuccessListener { documentReference ->

                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore Hata", "Veri yazma hatası: ${e.message}")
                    }
            } else {
                Log.d("OlayTakibi", "null değer.")
            }

        }

    }

    private fun verifyVerificationCode(verificationCode: String, verificationId: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MesajlarActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                    finish() // Kod aktivitesini kapat
                } else {
                    Toast.makeText(
                        this,
                        "Doğrulama başarısız oldu. Lütfen tekrar deneyin.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


    }

    fun onBackPressed(view: View) {
        val intent = Intent(this, NumActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)

    }

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.length == 1) {
                when (s) {
                    editText1.text -> {

                        editText2.requestFocus()
                    }
                    editText2.text -> {

                        editText3.requestFocus()
                    }
                    editText3.text -> {

                        editText4.requestFocus()
                    }
                    editText4.text -> {

                        editText5.requestFocus()
                    }
                    editText5.text -> {

                        editText6.requestFocus()
                    }
                }
            }

        }


        override fun afterTextChanged(s: Editable?) {

        }


    }

}
