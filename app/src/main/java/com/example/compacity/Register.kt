package com.example.compacity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Register : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // ---------------------------------------------------------------------------------------------
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")
        // ---------------------------------------------------------------------------------------------

        // ---------------------------------------------------------------------------------------------
        val txtName = findViewById<EditText>(R.id.name_Register)
        val txtAddress = findViewById<EditText>(R.id.address_Register)
        val txtEmail = findViewById<EditText>(R.id.email_Register)
        val txtPassword = findViewById<EditText>(R.id.password_Register)
        val txtBirthday = findViewById<EditText>(R.id.birthday_Register)

        val btnRegister = findViewById<Button>(R.id.btn_RegisterNewUser)
        // ---------------------------------------------------------------------------------------------

        // ---------------------------------------------------------------------------------------------
        btnRegister.setOnClickListener {

            val name = txtName.text.toString().trim()
            val address = txtAddress.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val birthday = txtBirthday.text.toString().trim()
            val userType = 2

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastId = snapshot.childrenCount // Obtiene el número total de registros
                    val nextId = (lastId + 1).toString() // Calcula el siguiente identificador

                    var userFound = false

                    // Coincide el correo electrónico
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
                        val newUser = User(name = name, email = email, password = password, birthday = birthday, address = address, userType = userType)
                        reference.child(nextId.toString()).setValue(newUser)
                            .addOnSuccessListener {
                                // Registro exitoso
                                Toast.makeText(this@Register, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Register, MainActivity::class.java))
                            }
                            .addOnFailureListener {
                                // Error al guardar los datos
                                Toast.makeText(this@Register, "Error al registrar. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                            }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Error en carga de datos
                    println("Error al leer datos: ${error.message}")
                }
            })

        }
        // ---------------------------------------------------------------------------------------------

    }
}

data class Users(val name: String? = null, val address: String? = null, val email: String? = null, val password: String? = null, val birthday: String? = null, val userType: Int? = null)