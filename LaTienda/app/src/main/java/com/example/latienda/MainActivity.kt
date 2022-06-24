package com.example.latienda

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener




class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var Email:    String = ""
    var Password: String = ""
    var RstPass:  String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnRegistro: Button = findViewById<Button>(R.id.btnregister)
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        var EmailEditText     = findViewById <EditText> (R.id.MainActivity_LoginEmail)      as EditText
        var PasswordEditText  = findViewById <EditText> (R.id.MainActivity_LoginPassword)   as EditText

        var botonInicio : Button = findViewById<Button>(R.id.btn_inicio_sesion)
        botonInicio.setOnClickListener {
            Email    = EmailEditText.text.toString()
            Password = PasswordEditText.text.toString()
            if (!Email.isEmpty() && !Password.isEmpty()){
                signIn()    // Iniciar Sesión.
            }
            else{
                Toast.makeText(this, "Ingrese Email y Password.", Toast.LENGTH_SHORT).show()
            }
        }

        var botonRstPass : Button = findViewById<Button>(R.id.btn_olvidoclave)
        botonRstPass.setOnClickListener {MsgResetPassword()}


    }//Final de la función

    private fun signIn() {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(Email, Password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }// [END sign_in_with_email]
    }

    fun MsgResetPassword(){

        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Reset Password")

        // recurso de diseño xml con un EditText
        val view = layoutInflater.inflate(R.layout.reset_password, null)
        val ResetEditText     = view.findViewById <EditText> (R.id.rstpassword)      as EditText
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val newList = ResetEditText.text
            var isValid = true
            if (newList.isBlank()) {
                isValid = false
            }
            if (isValid) {
                RstPass = ResetEditText.text.toString()
                println(RstPass)
                //validarcorreo()
                sendPasswordReset()
            }
            if (!isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }
        builder.show()
    }//Fin función btnCancelar

    fun validarcorreo(){
        println("*****************************************")
        println("El correo es: "+ RstPass)
        println("*****************************************")
    }

    fun sendPasswordReset() {
        // [START send_password_reset]
        val auth = FirebaseAuth.getInstance()
        val emailAddress = RstPass
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Email sent.", Toast.LENGTH_SHORT).show()
                }
            }
        // [END send_password_reset]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}//Final de la Clase