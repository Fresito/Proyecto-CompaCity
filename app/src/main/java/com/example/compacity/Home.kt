package com.example.compacity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    @SuppressLint("MissingInflatedId")
    private val productList: MutableList<Computers> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // --------------------------------------------------------------------------------------------------
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> Toast.makeText(applicationContext, "Inicio", Toast.LENGTH_SHORT).show()
                R.id.nav_profile -> {
                    startActivity(Intent(this, Profile::class.java))
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


        // --------------------------------------------------------------------------------------------------

        // Configurar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.productRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        // --------------------------------------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("computers") // Nombre de la tabla

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiamos la lista antes de agregar los nuevos productos
                productList.clear()

                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Computers::class.java)
                    product?.let { productList.add(it) }
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos: ${error.message}")
            }
        })
        // --------------------------------------------------------------------------------------------------
    }
}

class ProductAdapter(private val productList: List<Computers>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val computerTitle: TextView = itemView.findViewById(R.id.detail_title)
        private val computerDescription: TextView = itemView.findViewById(R.id.detail_characteristics)
        private val computerPrice: TextView = itemView.findViewById(R.id.detail_price)
        private val computerImage: ImageView = itemView.findViewById(R.id.detail_image)

        fun bind(computer: Computers) {
            computerTitle.text = computer.brand
            computerDescription.text = computer.description
            computerPrice.text = "$${computer.serialNumber}"
            Glide.with(itemView.context)
                .load(computer.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen de placeholder mientras se carga la imagen
                .error(R.drawable.logo_utvt) // Imagen en caso de error al cargar
                .into(computerImage)
        }
    }
}
