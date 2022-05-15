package com.example.ecomovingapp

import com.google.gson.Gson

class User (var email:String, var password:String) {

    var token:String? = null
    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }

}