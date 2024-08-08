package com.example.compacity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView

class Profile : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val btnGetOut = findViewById<Button>(R.id.btn_GetOut)

        btnGetOut.setOnClickListener {
            finish()
        }

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


        // --------------------------------------------------------------------------------------------------
        // Actualizar los datos del usuario en el header del menú lateral
        val headerView = navView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.header_nameUser)
        val userEmailTextView: TextView = headerView.findViewById(R.id.header_email)
        val userProfileImageView: de.hdodenhof.circleimageview.CircleImageView = headerView.findViewById(R.id.header_imageUser)

        val user = UserManager.user

        userNameTextView.text = user?.name
        userEmailTextView.text = user?.email
        if (user?.imageUrl != null) {
            Glide.with(this)
                .load(user.imageUrl)
                .into(userProfileImageView)
        } else {
            userProfileImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        // --------------------------------------------------------------------------------------------------

        val textView1 = findViewById<TextView>(R.id.txt_nameProfile)
        textView1.text = "Nombre: ${user?.name}"

        val textView2 = findViewById<TextView>(R.id.txt_addressProfile)
        textView2.text = "Direccion: ${user?.address}"

        val textView3 = findViewById<TextView>(R.id.txt_emailProfile)
        textView3.text = "Correo: ${user?.email}"

        val textView4 = findViewById<TextView>(R.id.txt_birthdayProfile)
        textView4.text = "Cumpleaños: ${user?.birthday}"

        val imageView = findViewById<ImageView>(R.id.img_profile)
        if (user?.imageUrl != null) {
            Glide.with(this)
                .load(user.imageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

    }
}
