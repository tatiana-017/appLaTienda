package com.example.latienda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import java.util.*
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.widget.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UserProfile: AppCompatActivity() {

    private var email       =""
    private var nombre      = ""
    private var documento   = ""
    private var direccion   = ""
    private var ciudad      = ""
    private var tipoUsuario = ""

    private lateinit var mDatabase: DatabaseReference

    private val SELECT_ACTIVITY     =50
    private var imageUri:   Uri?    = null
    private var IdPhoto:    String  = ""
    private var Photo:      String  = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        val user  = Firebase.auth.currentUser
        mDatabase = FirebaseDatabase.getInstance().getReference()
        user?.let {
            // Name, email address, and profile photo Url
            val Id  = user.uid
            IdPhoto = Id
            val photoUrl = user.photoUrl
            //Photo =photoUrl
            obtenerUsuario(Id)
        }

        var botonEditar : Button = findViewById<Button>(R.id.User_Profile_button_edit)
        botonEditar.setOnClickListener {

            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        var botonBorrar : Button = findViewById<Button>(R.id.btnDeleteProfile)
        botonBorrar.setOnClickListener {
            btnDelete()
        }

        var botonCerrar : Button = findViewById<Button>(R.id.btnCerrarSesion)
        botonCerrar.setOnClickListener {
            signOut()
        }

    }//Final de la función

    fun onclick_camera(view: android.view.View) {
        ImageController.SelectPhotoFromGallery(this, SELECT_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var ImagenView   = findViewById <ImageView> (R.id.UserImageView)   as ImageView
        when{
            requestCode==SELECT_ACTIVITY && resultCode==Activity.RESULT_OK -> {
                imageUri=data!!.data
                ImagenView.setImageURI(imageUri)
                val Folder: StorageReference = FirebaseStorage.getInstance().getReference().child("User")
                val file_name: StorageReference = Folder.child("PhotoUser" + imageUri!!.lastPathSegment)
                file_name.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["Url"] = java.lang.String.valueOf(uri)
                        mDatabase.child("Users").child(IdPhoto).child("Photo").setValue(hashMap)
                        Toast.makeText(baseContext, "Foto cargada exitosamente.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "KotlinActivity"
    }

    fun btnDelete(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Borrar Usuario")
        builder.setMessage("¿Está seguro?")

        builder.setPositiveButton("Aceptar") { _, _ -> deleteUser() }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false) /*Para dejar el mensaje fijo y que no se pueda cancelar sin dar una de las opciones disponibles*/
        dialog.show()
    }

    private fun deleteUser() {
        // [START delete_user]
        val user = Firebase.auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Usuario eliminado exitosamente.", Toast.LENGTH_LONG).show()

                }
            }
        mDatabase.child("Users").child(IdPhoto).removeValue()
        // [END delete_user]
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(baseContext, "Sesión Cerrada Exitosamente.", Toast.LENGTH_LONG).show()
    }

    private fun obtenerUsuario(id: String) {

        val mUserProfileEmail       = findViewById <TextView>  (R.id.UserProfileEmail)      as TextView
        val mUserProfileNombre      = findViewById <TextView>  (R.id.UserProfileNombre)     as TextView
        val mUserProfileDocumento   = findViewById <TextView>  (R.id.UserProfileDocumento)  as TextView
        val mUserProfileDireccion   = findViewById <TextView>  (R.id.UserProfileDireccion)  as TextView
        val mUserProfileCiudad      = findViewById <TextView>  (R.id.UserProfileCiudad)     as TextView
        var ImagenView              = findViewById <ImageView> (R.id.UserImageView)         as ImageView

        // Read from the database
        mDatabase.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    email       = dataSnapshot.child(id).child("Email").value.toString()
                    nombre      = dataSnapshot.child(id).child("Nombre").value.toString()
                    documento   = dataSnapshot.child(id).child("Documento").value.toString()
                    direccion   = dataSnapshot.child(id).child("Direccion").value.toString()
                    ciudad      = dataSnapshot.child(id).child("Ciudad").value.toString()
                    tipoUsuario = dataSnapshot.child(id).child("TipoUsuario").value.toString()
                    Photo       = dataSnapshot.child(id).child("Photo").child("Url").value.toString()
                    mUserProfileEmail.setText(email)
                    mUserProfileNombre.setText(nombre)
                    mUserProfileDocumento.setText(documento)
                    mUserProfileDireccion.setText(direccion)
                    mUserProfileCiudad.setText(ciudad)
                    //ImagenView.setImageURI(Photo)
                    Picasso.get()
                        .load(Photo)
                        .error(R.drawable.icon_image_not_available)
                        .into(ImagenView)
                    println("********************URL PHOTO********************")
                    println(Photo)
                    println("********************URL PHOTO********************")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }
}//Final de la Clase