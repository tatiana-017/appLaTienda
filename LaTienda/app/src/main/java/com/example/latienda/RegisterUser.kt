package com.example.latienda


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.HashMap
/*import com.google.firebase.referencecode.database.kotlin.models.Post*/

class RegisterUser: AppCompatActivity() {

    var Documento:   String = ""
    var Nombre:      String = ""
    var Email:       String = ""
    var Password1:   String = ""
    var Password2:   String = ""
    var Ciudad:      String = ""
    var Direccion:   String = ""
    var TipoUsuario: String = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var dbReference: FirebaseDatabase
    private val TAG = "ReadAndWriteSnippets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_user)

        var btnInicioSesion: Button = findViewById<Button>(R.id.btnsesion)
        btnInicioSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database.reference.child("Users")

        var DocumentoEditText   = findViewById <EditText> (R.id.register_documento)   as EditText
        var NombreEditText      = findViewById <EditText> (R.id.register_nombre)      as EditText
        var EmailEditText       = findViewById <EditText> (R.id.register_email)       as EditText
        var Password1EditText   = findViewById <EditText> (R.id.register_password1)   as EditText
        var Password2EditText   = findViewById <EditText> (R.id.register_password2)   as EditText
        var CiudadEditText      = findViewById <EditText> (R.id.register_ciudad)      as EditText
        var DireccionEditText   = findViewById <EditText> (R.id.register_direccion)   as EditText
        var TipoUsuarioEditText = findViewById <EditText> (R.id.register_tipoUsuario) as EditText

        var btnCreaCuenta: Button = findViewById<Button>(R.id.btn_register)
        btnCreaCuenta.setOnClickListener {

            Documento   = DocumentoEditText.text.toString()
            Nombre      = NombreEditText.text.toString()
            Email       = EmailEditText.text.toString()
            Password1   = Password1EditText.text.toString()
            Password2   = Password2EditText.text.toString()
            Ciudad      = CiudadEditText.text.toString()
            Direccion   = DireccionEditText.text.toString()
            TipoUsuario = TipoUsuarioEditText.text.toString()

            validarDatos()           //Llama a la funciòn que valida que todos los campos hayan sido diligenciados y valida la coincidencia del Password
        }
    }

    private fun validarDatos(){
        if (!Documento.isEmpty() && !Nombre.isEmpty() && !Email.isEmpty() && !Password1.isEmpty()
            && !Password2.isEmpty() && !Ciudad.isEmpty() && !Direccion.isEmpty() && !TipoUsuario.isEmpty()){

            if(Password1==Password2){       //Se valida que la contraseña ingresada sea la misma en ambos campos.
                if(Password1.length>=6){    //Como Firebase admite contraseñas con longitud mayor a 6 caracteres se hace esta validación.
                    registerUser()          // Se llama la función de registro de usuario en Firebase.
                    //writeNewUser(id)          // Llama a la función que almacena los datos del usuario en la BD de RealTime Database
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Longitud del Password debe ser mayor a 6 caracteres.", Toast.LENGTH_SHORT).show()}
            }
            else{
                Toast.makeText(this, "La contraseña no coincide.", Toast.LENGTH_SHORT).show()}
        }
        else{
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()}
    }

    private fun registerUser(){
        auth.createUserWithEmailAndPassword(Email,Password1).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                val userBD= user?.let { database.child(it?.uid) }
                if (userBD != null) {
                    userBD.child("Documento").setValue(Documento)
                    userBD.child("Nombre").setValue(Nombre)
                    userBD.child("Email").setValue(Email)
                    userBD.child("Password").setValue(Password1)
                    userBD.child("Ciudad").setValue(Ciudad)
                    userBD.child("Direccion").setValue(Direccion)
                    userBD.child("TipoUsuario").setValue(TipoUsuario)
                }
                val Id: String=userBD.toString()
                updateUI(user)
                println("***********************************************Registro Exitoso*******************************************************")
                signIn() //Inicia sesión automáticamente luego del reinicio
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
                println("***********************************************Registro Fallido*******************************************************")
            }
        }
    }

    private fun signIn(){
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(Email, Password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }// [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    /**** Inicialización de la BD ****/

    /*fun initializeDbRef() {
        // [START initialize_database_ref]
        database = Firebase.database.reference
        // [END initialize_database_ref]
    }*/

    /*private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }*/

    /**** [START rtdb_write_new_user] ****/
    /*private fun writeNewUser(userId: String){
        val users = User(Documento, Nombre, Email, Password1,Ciudad,Direccion,TipoUsuario)
        auth.signInWithEmailAndPassword(Email, Password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    database.child("users").child(userId).setValue(users)
                } else {
                    Toast.makeText(
                        baseContext, "Error al guardar.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }*/
    /**** [END rtdb_write_new_user] ****/

    /*fun writeNewUserWithTaskListeners(userId: String, name: String, email: String) {
        val user = User(name, email)

        // [START rtdb_write_new_user_task]
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                // Write was successful!
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
        // [END rtdb_write_new_user_task]
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue<Post>()
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }

    // [START write_fan_out]
    private fun writeNewPost(userId: String, username: String, title: String, body: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = database.child("posts").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val post = Post(userId, username, title, body)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/posts/$key" to postValues,
            "/user-posts/$userId/$key" to postValues
        )

        database.updateChildren(childUpdates)
    }
    // [END write_fan_out]


    // [START post_stars_transaction]
    private fun onStarClicked(postRef: DatabaseReference) {
        // [START_EXCLUDE]
        val uid = ""
        // [END_EXCLUDE]
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                    ?: return Transaction.success(mutableData)

                if (p.stars.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1
                    p.stars.remove(uid)
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1
                    p.stars[uid] = true
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError!!)
            }
        })
    }
    // [END post_stars_transaction]

    // [START post_stars_increment]
    private fun onStarClicked(uid: String, key: String) {
        val updates: MutableMap<String, Any> = HashMap()
        updates["posts/$key/stars/$uid"] = true
        updates["posts/$key/starCount"] = ServerValue.increment(1)
        updates["user-posts/$uid/$key/stars/$uid"] = true
        updates["user-posts/$uid/$key/starCount"] = ServerValue.increment(1)
        database.updateChildren(updates)
    }*/

}