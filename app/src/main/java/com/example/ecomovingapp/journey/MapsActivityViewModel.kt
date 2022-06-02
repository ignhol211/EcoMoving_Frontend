package com.example.ecomovingapp.journey

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ecomovingserver.ecomovingserver.AuthUser
import com.example.ecomovingapp.Error
import com.google.gson.Gson
import com.serverecomoving.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MapsActivityViewModel: ViewModel() {

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

    fun getAvailableVehicles(authUserToken:String){
        val client = OkHttpClient()
        val request = Request.Builder()

        request.url("http://10.0.2.2:8083/getAvailabledVehicles")
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
                    Log.d("2   ",body)
                    val gson = Gson()
                    val vehicleResponse = gson.fromJson(body, VehicleResponse::class.java)
                    Log.d("1   ",vehicleResponse.toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        setVehicleResponseInMainThread(vehicleResponse)
                    }
                }
            }
        })
    }

}