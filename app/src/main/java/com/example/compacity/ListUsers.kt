package com.example.compacity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ListUsers : AppCompatActivity() {

    private val userList: MutableList<Users> = mutableListOf()
    private val userKeys: MutableList<String> = mutableListOf()
    lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_users)

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
                R.id.navAdmin_NewProduct -> {
                    startActivity(Intent(this, NewProduct::class.java))
                    finish()
                }
                R.id.navAdmin_UpdateUserAndDeleteUser -> Toast.makeText(applicationContext, "Administracion de usuarios", Toast.LENGTH_SHORT).show()
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
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserAdapter(userList, userKeys)
        recyclerView.adapter = adapter
        // --------------------------------------------------------------------------------------------------


        // --------------------------------------------------------------------------------------------------
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users") // Nombre de la tabla

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiamos la lista antes de agregar los nuevos usuarios
                userList.clear()
                userKeys.clear() // Limpiar userKeys

                // Limpiar la lista de claves de usuario también
                val userKeys = mutableListOf<String>()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    val key = userSnapshot.key ?: continue
                    user?.let {
                        userList.add(it)
                        userKeys.add(key)
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged()

                // Configurar adaptador con claves de usuario
                (recyclerView.adapter as UserAdapter).setUserKeys(userKeys)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos: ${error.message}")
            }
        })
        // --------------------------------------------------------------------------------------------------

    }
}

class UserAdapter(
    private val userList: MutableList<Users>,
    private val userKeys: MutableList<String> // Cambiar a MutableList
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        val key = userKeys[position]
        holder.bind(user, key)
    }

    fun setUserKeys(keys: List<String>) {
        userKeys.clear()
        userKeys.addAll(keys)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameUser: TextView = itemView.findViewById(R.id.userName_ListUser)
        private val emailUser: TextView = itemView.findViewById(R.id.userEmail_ListUser)
        private val imageUser: ImageView = itemView.findViewById(R.id.img_ListUser)
        private val btnEditUser: Button = itemView.findViewById(R.id.btn_EditUser)
        private val btnDeleteUser: Button = itemView.findViewById(R.id.btn_DeleteUser)

        fun bind(user: Users, key: String) {
            nameUser.text = user.name
            emailUser.text = user.email
            Glide.with(itemView.context)
                .load(user.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.logo_utvt)
                .into(imageUser)

            btnEditUser.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditUser::class.java)
                intent.putExtra("USER_KEY", key) // Pasar la clave del usuario
                context.startActivity(intent)
            }

            // Agregar funcionalidad para eliminar el usuario con confirmación
            btnDeleteUser.setOnClickListener {
                val context = itemView.context
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Eliminar Usuario")
                builder.setMessage("¿Estás seguro de que deseas eliminar a este usuario?")
                builder.setPositiveButton("Eliminar") { dialog: DialogInterface, _: Int ->
                    // Confirmación de eliminación
                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("users").child(key)
                    reference.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Opcional: Mostrar un mensaje de éxito
                            Toast.makeText(context, "Usuario eliminado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            // Opcional: Mostrar un mensaje de error
                            Toast.makeText(context, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
                    // Cancelar la eliminación
                    dialog.dismiss()
                }
                builder.create().show()
            }

        }
    }
}
