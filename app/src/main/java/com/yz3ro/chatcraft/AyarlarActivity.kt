package com.yz3ro.chatcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class AyarlarActivity : AppCompatActivity() {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayarlar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setupBottomNavigation(this,bottomNav)
        bottomNav.menu.findItem(R.id.navigation_messages).setIcon(R.drawable.bos_mesaj)
        bottomNav.menu.findItem(R.id.navigation_settings).setIcon(R.drawable.dolu_ayarlar)
        bottomNav.menu.findItem(R.id.navigation_contacts).setIcon(R.drawable.bos_rehber)
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        val cikis = findViewById<ImageButton>(R.id.img_cikis)
        val pp_ad = findViewById<TextView>(R.id.pp_ad)
        val pp_num = findViewById<TextView>(R.id.pp_num)
        val profil_foto = findViewById<CircleImageView>(R.id.profil_foto) // Profil fotoğrafını göstereceğiniz ImageView
        val p_duzen = findViewById<Button>(R.id.profil_duzen)
        // Firestore'dan kullanıcı bilgilerini çekme
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("kullanicilar").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val phoneNumber = document.getString("numara")
                        val userName = document.getString("ad")
                        val ProfilFoto = document.getString("profilFotoUrl") // Profil fotoğrafının URL'sini alın

                        val upperCaseUserName = userName?.toUpperCase()
                        pp_ad.text = upperCaseUserName
                        pp_num.text = phoneNumber

                        // Profil fotoğrafını yükleyin

                    } else {
                        // Kullanıcı verileri bulunamadı
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore'dan veri çekerken hata oluştu
                }
        } else {
            // Kullanıcı oturum açmamış
        }
        p_duzen.setOnClickListener { intent = Intent(this,DuzenActivity::class.java)
        startActivity(intent)}
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("kullanicilar").document(userId)

            // Firestore'dan kullanıcı belgesini dinle
            userRef.addSnapshotListener { document, exception ->
                if (exception != null) {
                    // Hata durumu
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val ProfilFoto = document.getString("profilFotoUrl") // Yeni profil fotoğrafının URL'sini alın

                    // Profil fotoğrafını yükleyin
                    if (ProfilFoto != null) {
                        // Glide ile yeni URL'yi kullanarak profil fotoğrafını güncelleyin
                        Glide.with(this)
                            .load(ProfilFoto)
                            .into(profil_foto)
                    }
                }
            }
        } else {
            // Kullanıcı oturum açmamış
        }
        cikis.setOnClickListener {  val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Çıkış Onayı")
            alertDialogBuilder.setMessage("Uygulamadan çıkmak istediğinize emin misiniz?")

            // "Evet" butonu
            alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                finishAffinity()

            }

            // "Hayır" butonu
            alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show() }

    }
}
