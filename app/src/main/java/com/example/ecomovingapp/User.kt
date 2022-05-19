package com.example.ecomovingapp

import com.google.gson.Gson

class User (val email:String, val password:String) {

    var token:String? = null

    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }

}