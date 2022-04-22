package com.example.ecomovingapp

import com.google.gson.Gson

class User (val user:String, var password:String) {

    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }

}