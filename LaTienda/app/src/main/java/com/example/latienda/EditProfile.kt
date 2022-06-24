package com.example.latienda

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditProfile: AppCompatActivity() {

    var Documento:   String = ""
    var Nombre:      String = ""
    var Ciudad:      String = ""
    var Direccion:   String = ""
    var TipoUsuario: String = ""
    var Email:       String = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val user = Firebase.auth.currentUser
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference()

        user?.let {
            val Id  = user.uid
            obtenerUsuario(Id)
        }


        var botonCancelar : Button = findViewById<Button>(R.id.Menu_Edit_Profile_button_cancel)
        botonCancelar.setOnClickListener {btnCancelar()}

        var botonGuardar : Button = findViewById<Button>(R.id.Menu_Edit_Profile_button_save)
        botonGuardar.setOnClickListener {btnGuardar()}

    }//Final de la función

    fun btnCancelar(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("¿Está seguro de cancelar?")
        builder.setMessage("Se perderan los cambios")

        builder.setPositiveButton("Aceptar") { _, _ -> btncancelarOk() }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)/*Para dejar el mensaje fijo y que no se pueda cancelar sin dar una de las opciones disponibles*/
        dialog.show()
    }//Fin función btnCancelar

    fun btncancelarOk(){
        val intent = Intent(this, UserProfile::class.java)
        startActivity(intent)
    }

    fun btnGuardar(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Actualizar Datos")
        builder.setMessage("¿Actualizar cambios?")

        builder.setPositiveButton("Aceptar") { _, _ -> btnguardarOk() }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false) /*Para dejar el mensaje fijo y que no se pueda cancelar sin dar una de las opciones disponibles*/
        dialog.show()
    }//Fin función btnGuardar

    fun btnguardarOk(){

        var DocumentoEditText   = findViewById <EditText> (R.id.EditProfileDocumento)   as EditText
        var NombreEditText      = findViewById <EditText> (R.id.EditProfileNombre)      as EditText
        var CiudadEditText      = findViewById <EditText> (R.id.EditProfileCiudad)      as EditText
        var DireccionEditText   = findViewById <EditText> (R.id.EditProfileDireccion)   as EditText
        var TipoUsuarioEditText = findViewById <EditText> (R.id.EditProfileTipoUsuario) as EditText

        Documento   = DocumentoEditText.text.toString()
        Nombre      = NombreEditText.text.toString()
        Ciudad      = CiudadEditText.text.toString()
        Direccion   = DireccionEditText.text.toString()
        TipoUsuario = TipoUsuarioEditText.text.toString()

        database = Firebase.database.reference.child("Users")
        /** ********************Pegar aquí el resto de código requerido para almacenar la informaciòn en BD************** **/
        val user = auth.currentUser
        val userBD= user?.let { database.child(it?.uid) }
        if (userBD != null) {
            userBD.child("Documento").setValue(Documento)
            userBD.child("Nombre").setValue(Nombre)
            userBD.child("Ciudad").setValue(Ciudad)
            userBD.child("Direccion").setValue(Direccion)
            userBD.child("TipoUsuario").setValue(TipoUsuario)

            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
            Toast.makeText(this, "Actualización exitosa.", Toast.LENGTH_SHORT).show()
        }else {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
            Toast.makeText(baseContext, "Actualización fallida.", Toast.LENGTH_SHORT).show()
        }
    }//Fin función btnguardarOk

    private fun obtenerUsuario(id: String) {

        var NombreEditText      = findViewById <EditText> (R.id.EditProfileNombre)      as EditText
        var DocumentoEditText   = findViewById <EditText> (R.id.EditProfileDocumento)   as EditText
        var DireccionEditText   = findViewById <EditText> (R.id.EditProfileDireccion)   as EditText
        var CiudadEditText      = findViewById <EditText> (R.id.EditProfileCiudad)      as EditText
        var TipoUsuarioEditText = findViewById <EditText> (R.id.EditProfileTipoUsuario) as EditText

        // Read from the database
        database.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    Nombre      = dataSnapshot.child(id).child("Nombre").value.toString()
                    Documento   = dataSnapshot.child(id).child("Documento").value.toString()
                    Direccion   = dataSnapshot.child(id).child("Direccion").value.toString()
                    Ciudad      = dataSnapshot.child(id).child("Ciudad").value.toString()
                    TipoUsuario = dataSnapshot.child(id).child("TipoUsuario").value.toString()

                    NombreEditText.setText(Nombre)
                    DocumentoEditText.setText(Documento)
                    DireccionEditText.setText(Direccion)
                    CiudadEditText.setText(Ciudad)
                    TipoUsuarioEditText.setText(TipoUsuario)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
        // [END read_message]
    }

}//Final de la Clase