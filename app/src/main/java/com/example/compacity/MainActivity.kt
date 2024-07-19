package com.example.compacity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // -------------------------------------------------------------------------------------------------
        val txtEmail = findViewById<EditText>(R.id.text_email)
        val txtPassword = findViewById<EditText>(R.id.text_password)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        // -------------------------------------------------------------------------------------------------

        FirebaseApp.initializeApp(this) // Inicializacion de Firebase
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users") // Nombre de la tabla

        // -------------------------------------------------------------------------------------------------
        btnLogin.setOnClickListener {

            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            reference.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var userFound = false
                    var userType: Int? = null

                    // Coincide el correo electrónico y la contraseña
                    for (childSnapshot in snapshot.children) {
                        val user = childSnapshot.getValue(User::class.java)
                        if (user?.email == email && user.password == password) {
                            userFound = true
                            userType = user.userType
                            break
                        }
                    }

                    if (userFound) {
                        // Mostrar un mensaje de éxito (por ejemplo, un Toast)
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        when (userType) {
                            1 -> startActivity(Intent(this@MainActivity, Home::class.java))
                            2 -> startActivity(Intent(this@MainActivity, Home::class.java))
                            else -> Toast.makeText(this@MainActivity, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        // Mostrar un mensaje de error (por ejemplo, un Toast)
                        Toast.makeText(this@MainActivity, "Datos incorrectos. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Error en carga de datos
                    println("Error al leer datos: ${error.message}")
                }
            })

        }
        // -------------------------------------------------------------------------------------------------

        // -------------------------------------------------------------------------------------------------
        btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        // -------------------------------------------------------------------------------------------------

    }
}

data class User(val address: String? = null, val birthday: String? = null, val email: String? = null, val name: String? = null, val password: String? = null, val userType: Int? = null)