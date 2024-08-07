package com.example.compacity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Profile : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // --------------------------------------------------------------------------------------------------
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, Home::class.java))
                R.id.nav_profile -> Toast.makeText(applicationContext, "Perfil", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(applicationContext, "Cerrar sesión", Toast.LENGTH_SHORT).show()
            }
            true
        }
        // --------------------------------------------------------------------------------------------------

        val user = UserManager.user

        val textView1 = findViewById<TextView>(R.id.txt_nameProfile)
        textView1.text = "Nombre: ${user?.name}"

        val textView2 = findViewById<TextView>(R.id.txt_addressProfile)
        textView2.text = "Direccion: ${user?.address}"

        val textView3 = findViewById<TextView>(R.id.txt_emailProfile)
        textView3.text = "Correo: ${user?.email}"

        val textView4 = findViewById<TextView>(R.id.txt_birthdayProfile)
        textView4.text = "Cumpleaños: ${user?.birthday}"

    }
}