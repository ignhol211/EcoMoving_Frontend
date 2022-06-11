package com.example.ecomovingapp.journey

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ecomovingapp.R
import com.example.ecomovingapp.localdatabase.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource
import kotlinx.coroutines.runBlocking

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    companion object{
        var TOKEN = "initialValue"
        fun launch(context: Context, token:String){
            val intent = Intent(context,MapsActivity::class.java)
            intent.putExtra(TOKEN,token)
            context.startActivity(intent)
        }
    }
    private lateinit var map: GoogleMap
    private val viewModel : MapsActivityViewModel by viewModels()

    val USER = LatLng(39.47882734895583,-0.34249696880579)

    private lateinit var save:Button
    private lateinit var cancel:Button
    private lateinit var focus:Button
    private lateinit var locationDescription:EditText
    private lateinit var originPoint:EditText
    private lateinit var destinationPoint:EditText
    private lateinit var locations:Button
    private lateinit var dropDatabase:Button
    private lateinit var navigation:Button
    private lateinit var locationViaGPS: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        save = this.findViewById(R.id.button_save)
        cancel = this.findViewById(R.id.button_cancel)
        focus = this.findViewById(R.id.button_focus)
        locationDescription = this.findViewById(R.id.location_description)
        originPoint = this.findViewById(R.id.origin_point)
        destinationPoint = this.findViewById(R.id.destination_point)
        locations = this.findViewById(R.id.locations)
        dropDatabase = this.findViewById(R.id.drop_database)
        navigation = this.findViewById(R.id.navigation)
        locationViaGPS = this.findViewById(R.id.location_via_gps)

        val token = intent.getStringExtra(TOKEN)

        viewModel.initializeDatabase(this)

        viewModel.getAvailableVehicles(token.toString())

        initObserver()

        locations.setOnClickListener{
            runBlocking {
                viewModel.getAllLocations()
                showButtonsDropCancel()
                dropDatabase.setOnClickListener{
                    dropDatabase()
                }
                cancel.setOnClickListener{
                    hideButtonsDropCancel()
                }
            }
        }

        navigation.setOnClickListener(){
            val route = PolylineOptions().add(USER).add(LatLng(39.47335251375559,-0.3365186601877213))
            map.addPolyline(route)
        }

        locationViaGPS.setOnClickListener{
            originPoint.text = Editable.Factory.getInstance().newEditable(USER.toString())
        }

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

        viewModel.locations.observe(this){list->
            list.forEach { location->
                map.addMarker(
                    MarkerOptions().position(LatLng(location.latitude, location.longitude))
                        .icon(fromResource(R.mipmap.location))
                )
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

    fun focusOnUser(view:View){
        map.animateCamera(CameraUpdateFactory.newLatLng(USER))
    }

    override fun onMapClick(p0: LatLng) {
        val marker = MarkerOptions().position(p0)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        marker.draggable(true)
        marker.visible(true)
        map.addMarker(marker)
        showButtonsSaveCancel()
        save.setOnClickListener{
            viewModel.saveLocation(Location(0,p0.latitude,p0.longitude,locationDescription.text.toString()))
            hideButtonsSaveCancel()
            marker.visible(false)
        }
        cancel.setOnClickListener{
            hideButtonsSaveCancel()
            marker.visible(false)
        }
    }

    fun showButtonsSaveCancel(){
        save.visibility = View.VISIBLE
        cancel.visibility = View.VISIBLE
        locationDescription.visibility = View.VISIBLE

        focus.visibility = View.GONE
        originPoint.visibility = View.GONE
        destinationPoint.visibility = View.GONE
    }

    fun hideButtonsSaveCancel(){
        save.visibility = View.GONE
        cancel.visibility = View.GONE
        locationDescription.visibility = View.GONE
        dropDatabase.visibility = View.GONE

        focus.visibility = View.VISIBLE
        originPoint.visibility = View.VISIBLE
        destinationPoint.visibility = View.VISIBLE
    }

    fun showButtonsDropCancel(){
        dropDatabase.visibility = View.VISIBLE
        cancel.visibility = View.VISIBLE

        focus.visibility = View.GONE
    }

    fun hideButtonsDropCancel(){
        dropDatabase.visibility = View.GONE
        cancel.visibility = View.GONE

        focus.visibility = View.VISIBLE
    }

    fun dropDatabase(){
        viewModel.dropDatabase()
        hideButtonsDropCancel()
    }
}