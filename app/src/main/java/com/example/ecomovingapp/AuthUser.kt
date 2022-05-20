package com.ecomovingserver.ecomovingserver

import com.example.ecomovingapp.User
import com.google.gson.Gson

class AuthUser(user: User) {

    var email:String = user.email
    var token:String? = user.token

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

}