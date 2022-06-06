package com.example.ecomovingapp.journey

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.ecomovingapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    companion object{
        var TOKEN = "initialValue"
        fun launch(context: Context, token:String){
            val intent = Intent(context,MapsActivity::class.java)
            intent.putExtra(TOKEN,token)
            context.startActivity(intent)
        }
    }

    lateinit var map: GoogleMap
    val USER = LatLng(39.481106, -0.340987)
    private val viewModel : MapsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()

        val token = intent.getStringExtra(TOKEN)

        viewModel.initializeDatabase(this)

        viewModel.getAvailableVehicles(token.toString())

        initObserver()
    }

    private fun initObserver() {
        viewModel.vehicleResponse.observe(this) {
            it?.let {
                it.vehicles.forEach { vehicle ->
                    map.addMarker(
                        MarkerOptions().position(LatLng(vehicle.latitude, vehicle.longitude))
                            .icon(fromResource(R.mipmap.car))
                    )
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply{
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.isZoomControlsEnabled = true
            moveCamera(CameraUpdateFactory.newLatLngZoom(USER,15f))
            addMarker(MarkerOptions().position(USER).title("User")
                .icon(fromResource(R.mipmap.user))
            )
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
            map.uiSettings.isCompassEnabled = true
        }
        map.setOnMapClickListener(this)
    }

    fun saveLocation(view: View){
        map.moveCamera(CameraUpdateFactory.newLatLng(USER))
    }

    fun focusOnUser(view:View){
        map.animateCamera(CameraUpdateFactory.newLatLng(USER))
    }

    fun addMarker(view:View){
        map.addMarker((MarkerOptions().position(map.cameraPosition.target)))
    }

    override fun onMapClick(p0: LatLng) {
        map.addMarker(MarkerOptions().position(p0)
            .icon(fromResource(R.mipmap.car)))
    }
}