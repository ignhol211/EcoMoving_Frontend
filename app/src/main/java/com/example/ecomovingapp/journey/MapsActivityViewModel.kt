package com.example.ecomovingapp.journey

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.localdatabase.AppDatabase
import com.example.ecomovingapp.localdatabase.Location
import com.example.ecomovingapp.localdatabase.LocationDao
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MapsActivityViewModel: ViewModel() {

    private lateinit var db: AppDatabase
    private lateinit var locationDao: LocationDao

    private val _vehicleResponse by lazy { MediatorLiveData<VehicleResponse>() }
    val vehicleResponse : MediatorLiveData<VehicleResponse>
        get() = _vehicleResponse

    suspend fun setVehicleResponseInMainThread(value:VehicleResponse) = withContext(Dispatchers.Main){
        _vehicleResponse.value = value
    }

    private val _error by lazy { MediatorLiveData<Error>() }
    val error : LiveData<Error>
        get() = _error

    suspend fun setErrorInMainThread(value:Error) = withContext(Dispatchers.Main){
        _error.value = value
    }

    private val _locations by lazy { MediatorLiveData<List<Location>>() }
    val locations : LiveData<List<Location>>
        get() = _locations

    suspend fun setLocationsInMainThread(location:List<Location>) = withContext(Dispatchers.Main){
        _locations.value = location
    }

    fun initializeDatabase(context: Context){
        db = Room.databaseBuilder(context,AppDatabase::class.java,"locations").build()
        locationDao = db.locationDao()
    }

    fun saveLocation(location: Location){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                locationDao.insertOne(location)
            }
        }
    }

    fun dropDatabase(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                locationDao.emptyDatabase()
            }
        }
    }

    suspend fun getAllLocations(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                setLocationsInMainThread(locationDao.getAll())
            }
        }
    }


    fun getAvailableVehicles(authUserToken:String){
        val client = OkHttpClient()
        val request = Request.Builder()

        request.url("http://10.0.2.2:8083/getVehicles")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = authUserToken.toRequestBody(mediaType)
        request.post(requestBody)

        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    setErrorInMainThread(Error.error1)
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    val gson = Gson()
                    val vehicleResponse = gson.fromJson(body, VehicleResponse::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        setVehicleResponseInMainThread(vehicleResponse)
                    }
                }
            }
        })
    }

}