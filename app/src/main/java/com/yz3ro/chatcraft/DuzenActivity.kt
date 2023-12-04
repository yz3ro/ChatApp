package com.yz3ro.chatcraft

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*

class DuzenActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duzen)
        val yeni_ad = findViewById<EditText>(R.id.editTextKisiAdi)
        val kaydet = findViewById<Button>(R.id.btnOnayla)
        val profil_foto2 = findViewById<CircleImageView>(R.id.profil_foto1)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        profil_foto2.setOnClickListener { launchGallery() }
        kaydet.setOnClickListener {
            val storageReference = FirebaseStorage.getInstance().reference
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val yeniAd = yeni_ad.text.toString()
            if (yeniAd.isNotEmpty()) {
                // Kullanıcı adı boş değilse Firestore'daki kullanıcı belgesini güncelle
                updateUserDisplayName(yeniAd)
            } else {
                // Kullanıcı adı boşsa kullanıcıyı uyarın
                Toast.makeText(this, "Kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userId = user.uid

            val userDocRef = db.collection("kullanicilar").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Firestore belgesi varsa, profilFotoURL alanını al
                        val profilFotoUrl = documentSnapshot.getString("profilFotoURL")

                        // Şimdi Glide ile ImageView'a resmi yükle
                        if (profilFotoUrl != null) {
                            Glide.with(this)
                                .load(profilFotoUrl)
                                .into(profil_foto2)
                        }
                    } else {
                        // Firestore belgesi yoksa, kullanıcıya bilgi ver
                        Toast.makeText(this, "Profil fotoğrafı bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore'dan belge çekme sırasında bir hata oluştu
                    Toast.makeText(this, "Hata: $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun launchGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            // Seçilen resmin Uri'sini al
            val selectedImageUri: Uri = data.data!!

            // Firestore'a kaydedilecek bir yol oluştur
            storageReference = FirebaseStorage.getInstance().reference
            val user = FirebaseAuth.getInstance().currentUser
            user?.uid?.let { uid ->
                val imagePath = "profilImages/$uid.jpg"
                val imageRef = storageReference!!.child(imagePath)

                // Resmi Firestore'a yükle
                imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Yükleme başarılı oldu
                        // Firestore'dan profil fotoğrafının URL'sini al
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            // Firestore'daki kullanıcının profil fotoğrafını güncelle
                            updateImageUrlInFirestore(imageUrl)
                        }
                    }
                    .addOnFailureListener { e ->
                        // Yükleme başarısız oldu
                        Toast.makeText(this, "Error uploading image: $e", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun updateImageUrlInFirestore(imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            // Kullanıcının UID'sini kullanarak Firestore belgesini alın
            val userDocRef = db.collection("kullanicilar").document(uid)

            // Yeni verileri hazırla
            val updatedData = hashMapOf(
                "profilFotoURL" to imageUrl
            )
            // Firestore belgesini güncelle
            userDocRef
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    // Başarılı
                    Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Hata
                    Toast.makeText(this, "Error updating profile photo: $e", Toast.LENGTH_SHORT).show()
                }
        }

    }
    private fun updateUserDisplayName(yeniAd: String) {
        val kullanici = FirebaseAuth.getInstance().currentUser
        kullanici?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(yeniAd)
                .build()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kullanıcı adı güncelleme işlemi başarılı
                    // Firestore'daki kullanıcı belgesini güncelleme işlemine yönlendirme
                    updateFirestoreDisplayName(yeniAd)
                } else {
                    // Kullanıcı adı güncelleme işlemi başarısız oldu
                    Toast.makeText(this, "Kullanıcı adını güncelleme başarısız", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun updateFirestoreDisplayName(yeniAd: String) {
        val db = FirebaseFirestore.getInstance()
        val kullanici = FirebaseAuth.getInstance().currentUser
        kullanici?.uid?.let { uid ->
            val userDocRef = db.collection("kullanicilar").document(uid)

            userDocRef
                .update("ad", yeniAd)
                .addOnSuccessListener {
                    // Firestore'daki kullanıcı adı güncelleme işlemi başarılı
                    Toast.makeText(this, "Kullanıcı adı güncellendi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Hata durumu
                    Toast.makeText(this, "Kullanıcı adını güncelleme başarısız", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

