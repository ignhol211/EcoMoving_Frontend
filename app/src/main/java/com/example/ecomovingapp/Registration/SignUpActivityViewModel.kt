package com.example.ecomovingapp.Registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.ecomovingapp.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpActivityViewModel : ViewModel(){

    private val _user by lazy { MediatorLiveData<User>() }
    val user : LiveData<User>
        get() = _user

    suspend fun setUserInMainThread(value: User) = withContext(Dispatchers.Main){
        _user.value = value
    }

}