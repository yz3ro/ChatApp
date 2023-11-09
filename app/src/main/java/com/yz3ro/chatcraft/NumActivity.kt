package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class NumActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var tam_num = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_num)

        auth = FirebaseAuth.getInstance()
        val img_btn = findViewById<ImageButton>(R.id.imgbtn_ileri)


        img_btn.setOnClickListener {
            val countryCodePicker = findViewById<CountryCodePicker>(R.id.countyCodePicker)
            val countryCode = countryCodePicker.selectedCountryCodeWithPlus
            val txt_num = findViewById<EditText>(R.id.txt_num)
            val num = txt_num.text.toString()
            tam_num = "$countryCode$num"
            sendVerificationCode(tam_num)



        }

    }

    private fun sendVerificationCode(telefon_num: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(telefon_num)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Otomatik doğrulama tamamlandığında yapılacak işlemler
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Doğrulama hatası oluştuğunda yapılacak işlemler
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Doğrulama kodunu başka activity'ye aktar
                    val kullaniciAdi = intent.getStringExtra("kullaniciAdi")
                    val intent = Intent(this@NumActivity, KodActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("tam_num", telefon_num)
                    intent.putExtra("kullaniciAdi", kullaniciAdi)
                    startActivity(intent)
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun onBackPressed(view: View) {
        val intent= Intent(this,AdActivity::class.java)

        startActivity(intent)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }
}
