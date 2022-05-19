package com.example.ecomovingapp.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.ecomovingapp.Error
import com.example.ecomovingapp.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class SignUpActivityViewModel : ViewModel(){

    private val _user by lazy { MediatorLiveData<User>() }
    val user : LiveData<User>
        get() = _user

    suspend fun setUserInMainThread(value: User) = withContext(Dispatchers.Main){
        _user.value = value
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

    fun signUp(user: User){

        val client = OkHttpClient()
        //val request = Request.Builder()

        val formBody: RequestBody = FormBody.Builder()
            .add("email", user.email)
            .add("password", user.password)
            .build()

        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8083/register")
            .post(formBody)
            .addHeader("header","Content-type:application/json; charset=utf-8")
            .build()

        //request.url("http://10.0.2.2:8083/register").post(myUser.toString().toRequestBody())

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    val error = Error(1,"Fail to connect")
                    setErrorInMainThread(error)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val body = responseBody.string()

                    val gson = Gson()

                    val userToMainThread = gson.fromJson(body, User::class.java)

                    CoroutineScope(Dispatchers.Main).launch {
                        setUserInMainThread(userToMainThread)
                    }
                }
            }
        })
    }

    fun validateUserAndPassword(userId: String,password: String) {
        if(isUserOk(userId) && isPasswordOk(password)){
            CoroutineScope(Dispatchers.Main).launch {
                setIsVisibleInMainThread(true)
            }
        }else{
            CoroutineScope(Dispatchers.Main).launch {
                setIsVisibleInMainThread(false)
                val error = Error(2,"Invalid user or password")
                setErrorInMainThread(error)
            }
        }
    }

    private fun isUserOk(userId:String) : Boolean {
        val regex = Regex("[a-z]{5}$")
        return regex.matches(userId)
    }

    private fun isPasswordOk(password:String) : Boolean{
        val regex = Regex("[a-zA-Z0-9]{8}$")
        return  regex.matches(password)
    }

}