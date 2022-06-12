package com.example.ecomovingapp.journey

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
    private lateinit var GLOBALTOKEN:String

    val USER = LatLng(39.47882734895583,-0.34249696880579)

    private lateinit var save:Button
    private lateinit var cancel:Button
    private lateinit var focus:Button
    private lateinit var locationDescription:EditText
    private lateinit var originPoint:EditText
    private lateinit var destinationPoint:EditText
    private lateinit var locations:Button
    private lateinit var dropDatabase:Button
    private lateinit var calculate:Button
    private lateinit var locationViaGPS: Button
    private lateinit var acceptRoute:Button

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
        calculate = this.findViewById(R.id.navigation)
        locationViaGPS = this.findViewById(R.id.location_via_gps)
        acceptRoute = this.findViewById(R.id.accept)

        GLOBALTOKEN = intent.getStringExtra(TOKEN).toString()

        viewModel.initializeDatabase(this)

        viewModel.getAvailableVehicles(GLOBALTOKEN)

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

        calculate.setOnClickListener(){
            showRoute(USER,LatLng(39.47335251375559,-0.3365186601877213))
            showAcceptParameters(10,900)
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
        map.addMarker(marker)
        showButtonsSaveCancel()
        locationViaGPS.visibility = View.GONE
        locations.visibility = View.GONE
        save.setOnClickListener{
            viewModel.saveLocation(Location(0,p0.latitude,p0.longitude,locationDescription.text.toString()))
            removeMarkers()
            hideButtonsSaveCancel()
        }
        cancel.setOnClickListener{
            hideButtonsSaveCancel()
            removeMarkers()
        }
    }

    fun removeMarkers(){
        map.clear()
        viewModel.getAvailableVehicles(GLOBALTOKEN)
        map.addMarker(MarkerOptions().position(USER).title("User")
            .icon(fromResource(R.mipmap.user)))
        locations.visibility = View.VISIBLE
        locationViaGPS.visibility = View.VISIBLE
        cancel.visibility = View.GONE
        acceptRoute.visibility = View.GONE
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
        /**
         * Empty local database
         */
        viewModel.dropDatabase()
        hideButtonsDropCancel()
        removeMarkers()
    }

    fun showRoute(originPoint:LatLng,destinationPoint:LatLng){
        /**
         * Show route between origin and destination
         * @param originPoint:LatLng
         * @param destinationPoint:LatLng
         */
        val route = PolylineOptions().add(originPoint).add(destinationPoint)
        map.addPolyline(route)
    }

    fun showAcceptParameters(distance:Int, duration:Int){
        cancel.visibility = View.VISIBLE
        acceptRoute.visibility = View.VISIBLE

        originPoint.visibility = View.GONE
        destinationPoint.visibility = View.GONE
        locationViaGPS.visibility = View.GONE
        locations.visibility = View.GONE
        hideButtonsSaveCancel()
        cancel.visibility = View.VISIBLE

        cancel.setOnClickListener{
            hideButtonsSaveCancel()
            removeMarkers()
        }

        acceptRoute.setOnClickListener{
            val marker = MarkerOptions().position(LatLng(39.46736890994236,-0.35045843571424484))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            marker.draggable(true)
            marker.visible(true)
            map.addMarker(marker)

            val destinationPoint = LatLng(39.46736890994236,-0.35045843571424484)

            val route = PolylineOptions().add(USER).add(destinationPoint).color(R.color.ecomoving_darkgreen)
            map.addPolyline(route)

            showAlert(calculatePrice(distance,duration))

        }
    }

    fun calculatePrice(distance:Int, duration:Int):Int{
        /**
         * Function to calculate the price of the route define by user
         * Distance and duration are use to calculate price
         * @param distance:Int between origin point and destination point
         * @param duration:Int trip duration in seconds
         *
         * @return price:Int
         */
        return distance*duration/10
    }

    fun showAlert(price:Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Price: $price")
        builder.setPositiveButton("Yes"){_,_ ->}
        builder.setNegativeButton("No"){_,_ ->removeMarkers()}
        builder.create()
        builder.show()
    }
}