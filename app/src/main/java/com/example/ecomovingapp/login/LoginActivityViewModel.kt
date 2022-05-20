package com.example.ecomovingapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ecomovingserver.ecomovingserver.AuthUser
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivityViewModel: ViewModel() {

    private val _authUser by lazy { MediatorLiveData<AuthUser>() }
    val authUser : LiveData<AuthUser>
        get() = _authUser

    suspend fun setAuthUserInMainThread(value: AuthUser) = withContext(Dispatchers.Main){
        _authUser.value = value
    }

    private val _error by lazy { MediatorLiveData<Error>() }
    val error : LiveData<Error>
        get() = _error

    suspend fun setErrorInMainThread(value:Error) = withContext(Dispatchers.Main){
        _error.value = value
    }

    private val _isVisible by lazy { MediatorLiveData<Boolean>() }
    val isVisible : LiveData<Boolean>
        get() = _isVisible

    suspend fun setIsVisibleInMainThread(value : Boolean) = withContext(Dispatchers.Main){
        _isVisible.value = value
    }

    fun login(user:User){

        println(user.toString())

        val client = OkHttpClient()
        val request = Request.Builder()

        request.url("http://10.0.2.2:8083/login")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = user.toString().toRequestBody(mediaType)
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
                    val userToMainThread = gson.fromJson(body, AuthUser::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                            setAuthUserInMainThread(userToMainThread)
                    }
                }
            }
        })
    }

    fun validateUserAndPassword(userName: String, password: String) {
        if(isUserOk(userName) && isPasswordOk(password)){
            CoroutineScope(Dispatchers.Main).launch {
                setIsVisibleInMainThread(false)
            }
        }else{
            CoroutineScope(Dispatchers.Main).launch {
                setIsVisibleInMainThread(true)
            }
        }
    }

    private fun isUserOk(email:String) : Boolean {
        val regex = Regex("[a-z]{5}$")
        return regex.matches(email)
    }

    private fun isPasswordOk(password:String) : Boolean{
        val regex = Regex("[a-zA-Z0-9]{8}$")
        return regex.matches(password)
    }

}