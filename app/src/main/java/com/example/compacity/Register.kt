package com.example.compacity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Register : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // ---------------------------------------------------------------------------------------------
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")
        storageReference = FirebaseStorage.getInstance().reference.child("user_images")
        // ---------------------------------------------------------------------------------------------

        // ---------------------------------------------------------------------------------------------
        val txtName = findViewById<EditText>(R.id.name_Register)
        val txtAddress = findViewById<EditText>(R.id.address_Register)
        val txtEmail = findViewById<EditText>(R.id.email_Register)
        val txtPassword = findViewById<EditText>(R.id.password_Register)
        val txtBirthday = findViewById<EditText>(R.id.birthday_Register)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val btnRegister = findViewById<Button>(R.id.btn_RegisterNewUser)
        // ---------------------------------------------------------------------------------------------

        // ---------------------------------------------------------------------------------------------
        imgProfile.setOnClickListener {
            selectImage()
        }
        btnRegister.setOnClickListener {

            val name = txtName.text.toString().trim()
            val address = txtAddress.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val birthday = txtBirthday.text.toString().trim()
            val userType = 2

            reference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastId = snapshot.childrenCount
                    val nextId = (lastId + 1).toString()

                    var userFound = false

                    for (childSnapshot in snapshot.children) {
                        val user = childSnapshot.getValue(Users::class.java)
                        if (user?.email == email) {
                            userFound = true
                            break
                        }
                    }

                    if (userFound) {
                        Toast.makeText(this@Register, "El correo ya está registrado. Inténtalo con otro correo.", Toast.LENGTH_SHORT).show()
                    } else {
                        uploadImageAndRegisterUser(name, email, password, birthday, address, userType, reference.child(nextId))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error al leer datos: ${error.message}")
                }


            })

        }
        // ---------------------------------------------------------------------------------------------

    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            val imgProfile = findViewById<ImageView>(R.id.imgProfile)
            imgProfile.setImageURI(imageUri)
        }
    }

    private fun uploadImageAndRegisterUser(
        name: String, email: String, password: String, birthday: String,
        address: String, userType: Int, userRef: DatabaseReference
    ) {
        val imageRef = storageReference.child(System.currentTimeMillis().toString())
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val newUser = Users(name = name, email = email, password = password, birthday = birthday, address = address, userType = userType, imageUrl = uri.toString())
                userRef.setValue(newUser)
                    .addOnSuccessListener {
                        Toast.makeText(this@Register, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Register, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@Register, "Error al registrar. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir imagen. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
        }
    }

}

