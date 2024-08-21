package com.example.compacity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class EditUser : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    private lateinit var userKey: String
    private lateinit var storageReference: StorageReference
    private lateinit var selectedImageUri: Uri
    private var isImageSelected: Boolean = false
    private var editUserType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)

        val editUserName = findViewById<EditText>(R.id.name_Update)
        val editUserEmail = findViewById<EditText>(R.id.email_Update)
        val editUserBirthday = findViewById<EditText>(R.id.birthday_Update)
        val editUserAddress = findViewById<EditText>(R.id.address_Update)
        val editUserPassword = findViewById<EditText>(R.id.password_Update)
        val editUserImage = findViewById<CircleImageView>(R.id.imageUser_Update)
        val btnUpdate = findViewById<Button>(R.id.btn_UpdateUser)
        val btnBackPage = findViewById<ImageButton>(R.id.btn_backPage)

        userKey = intent.getStringExtra("USER_KEY") ?: return

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users").child(userKey)
        storageReference = FirebaseStorage.getInstance().reference.child("user_images")

        reference.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(Users::class.java)
            user?.let {
                editUserName.setText(it.name)
                editUserEmail.setText(it.email)
                editUserBirthday.setText(it.birthday)
                editUserAddress.setText(it.address)
                editUserPassword.setText(it.password)
                editUserType = it.userType!!

                if (user.imageUrl != null) {
                    Glide.with(this)
                        .load(user.imageUrl)
                        .into(editUserImage)
                } else {
                    editUserImage.setImageResource(R.drawable.ic_launcher_foreground)
                }

            }
        }

        btnBackPage.setOnClickListener {
            finish()
        }

        editUserImage.setOnClickListener {
            selectImage()
        }

        btnUpdate.setOnClickListener {
            val name = editUserName.text.toString()
            val email = editUserEmail.text.toString()
            val birthday = editUserBirthday.text.toString()
            val address = editUserAddress.text.toString()
            val password = editUserPassword.text.toString()

            if (name.isNotBlank() && email.isNotBlank()) {
                // Si se seleccionó una nueva imagen
                if (isImageSelected) {
                    val imageRef = storageReference.child(UUID.randomUUID().toString())
                    imageRef.putFile(selectedImageUri).addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val updatedUser = Users(name, address, email, password, birthday, editUserType, uri.toString())
                            reference.setValue(updatedUser)
                            finish() // Cierra la actividad después de guardar
                        }
                    }
                } else {
                    // Si no se seleccionó una nueva imagen
                    val updatedUser = Users(name, address, email, password, birthday, editUserType, null)
                    reference.setValue(updatedUser)
                    finish() // Cierra la actividad después de guardar
                }
            }

        }

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data ?: return
            isImageSelected = true
            val editUserImage = findViewById<CircleImageView>(R.id.imageUser_Update)
            editUserImage.setImageURI(selectedImageUri) // Muestra la imagen seleccionada
        }
    }

}