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
        kaydet.setOnClickListener { uploadImage()
            val storageReference = FirebaseStorage.getInstance().reference
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val profileImageRef = storageReference.child("profilFotoUrl/$uid.jpg")

            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                // Glide kullanarak resmi ImageView'da gösterin
                Glide.with(this)
                    .load(downloadUrl)
                    .into(profil_foto2)
            }
            val yeniAd = yeni_ad.text.toString()
            if (yeniAd.isNotEmpty()) {
                // Kullanıcı adı boş değilse Firestore'daki kullanıcı belgesini güncelle
                updateUserDisplayName(yeniAd)
            } else {
                // Kullanıcı adı boşsa kullanıcıyı uyarın
                Toast.makeText(this, "Kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Firebase Storage'dan URL'yi alın ve Firestore'a kaydedin
                    val photoUrl = downloadUri.toString()
                    saveImageUrlToFirestore(photoUrl)
                } else {
                    // Hata durumunu ele alabilirsiniz
                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener {
                // Hata durumunu ele alabilirsiniz
                Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val profil_foto2 = findViewById<ImageView>(R.id.profil_foto1)
                profil_foto2.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            // Kullanıcının UID'sini kullanarak Firestore belgesini alın
            val userDocRef = db.collection("kullanicilar").document(uid)

            // Firestore belgesine profil fotoğrafı URL'sini ekleyin
            userDocRef
                .update("profilFotoURL", imageUrl)
                .addOnSuccessListener {
                    // Başarılı
                    Toast.makeText(this, "Image Uploaded and URL Saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Hata
                    Toast.makeText(this, "Error Saving URL: $e", Toast.LENGTH_SHORT).show()
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

