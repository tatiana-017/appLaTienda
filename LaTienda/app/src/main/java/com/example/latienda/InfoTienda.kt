package com.example.latienda

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InfoTienda : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var fuseLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var map:GoogleMap
    var nombre:     String =""
    var direccion:  String =""
    var telefono:   String =""
    var tiendaId:   String ="1"

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_tienda)

        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database.reference

        var NombreEditText         = findViewById <EditText> (R.id.nombreTienda)    as EditText
        var DireccionEditText      = findViewById <EditText> (R.id.direccionTienda) as EditText
        var TelefonoEditText       = findViewById <EditText> (R.id.telefonoTienda)  as EditText

        var btnGuardarTienda: Button = findViewById<Button>(R.id.guardarTienda)
        btnGuardarTienda.setOnClickListener {

            nombre         = NombreEditText.text.toString()
            direccion      = DireccionEditText.text.toString()
            telefono       = TelefonoEditText.text.toString()

            validarDatos()           //Llama a la funciòn que valida que todos los campos hayan sido diligenciados y valida la coincidencia del Password
        }

        createFragment()
        fuseLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun validarDatos(){
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty()){
            writeNewUser()          // Llama a la función que almacena los datos del usuario en la BD de RealTime Database
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()}
    }

    private fun writeNewUser(){
        val tendero = Tendero(nombre, direccion, telefono)
        database.child("Tendero").child(tiendaId).setValue(tendero)

    }

    fun printMessage (view:View)
    {

    }

    private fun createFragment(){
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMarkerClickListener(this)
        map.uiSettings.isZoomControlsEnabled = true

        setUpMap()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fuseLocationClient.lastLocation.addOnSuccessListener(this) {location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,13f))
            }

        }
    }


}