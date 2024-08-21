package com.example.compacity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NewProduct : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_product)

        // --------------------------------------------------------------------------------------------------
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayoutAdmin)
        val navView: NavigationView = findViewById(R.id.nav_viewAdmin)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navAdmin_profile -> {
                    startActivity(Intent(this, NewProduct::class.java))
                    finish()
                }
                R.id.navAdmin_NewProduct -> Toast.makeText(applicationContext, "Nuevo Producto", Toast.LENGTH_SHORT).show()
                R.id.navAdmin_UpdateUserAndDeleteUser -> {
                    startActivity(Intent(this, ListUsers::class.java))
                    finish()
                }
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

        // ---------------------------------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("computers")
        storageReference = FirebaseStorage.getInstance().reference.child("computers_images")
        // ---------------------------------------------------------------------------------------------
        val txtBrand = findViewById<EditText>(R.id.brand_Computer)
        val txtDescription = findViewById<EditText>(R.id.description_Computer)
        val txtPrice = findViewById<EditText>(R.id.price_Computer)
        val btnNewProduct = findViewById<Button>(R.id.btn_NewProduct)
        val imgComputer = findViewById<ImageView>(R.id.img_Computer)
        // ---------------------------------------------------------------------------------------------
        imgComputer.setOnClickListener {
            selectImage()
        }
        btnNewProduct.setOnClickListener {

            val brand = txtBrand.text.toString().trim()
            val description = txtDescription.text.toString().trim()
            val model = "Nuevo"
            val releaseDate = "2024/09/09"
            val serialNumber = txtPrice.text.toString().trim()

            reference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastId = snapshot.childrenCount
                    val nextId = (lastId + 1).toString()

                    uploadImageAndRegisterUser(brand, description, model, releaseDate, serialNumber, reference.child(nextId))
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
            val imgComputer = findViewById<ImageView>(R.id.img_Computer)
            imgComputer.setImageURI(imageUri)
        }
    }

    private fun uploadImageAndRegisterUser(
        brand: String, description: String, model: String, releaseDate: String,
        serialNumber: String, userRef: DatabaseReference
    ) {
        val imageRef = storageReference.child(System.currentTimeMillis().toString())
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val newComputer = Computers(brand = brand, description = description, model = model, releaseDate = releaseDate, serialNumber = serialNumber, imageUrl = uri.toString())
                userRef.setValue(newComputer)
                    .addOnSuccessListener {
                        Toast.makeText(this@NewProduct, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@NewProduct, Home::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@NewProduct, "Error al registrar. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir imagen. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
        }
    }

}