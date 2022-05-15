package com.example.ecomovingapp.journey

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ecomovingapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    lateinit var map: GoogleMap
    val UPV = LatLng(39.481106,-0.340987)
    
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap.apply {
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            uiSettings.isZoomControlsEnabled = false
            moveCamera(CameraUpdateFactory.newLatLngZoom(UPV,15f))
            addMarker(MarkerOptions().position(UPV).title("UPV")
                .snippet("Univerisdad Politécnica de Valencia")
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_compass))
                .anchor(0.5f,0.5f))

        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
            map.uiSettings.isCompassEnabled = true
        }
        map.setOnMapClickListener(this)
    }

    fun moveCamera(view: View){
        map.moveCamera(CameraUpdateFactory.newLatLng(UPV))
    }

    fun animateCamera(view: View){
        map.animateCamera(CameraUpdateFactory.newLatLng(UPV))
    }

    fun addMarker(view:View){
        map.addMarker(MarkerOptions().position(map.cameraPosition.target))
    }

    override fun onMapClick(point:LatLng){
        map.addMarker(MarkerOptions().position(point)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
    }
}



