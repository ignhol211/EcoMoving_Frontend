package com.example.ecomovingapp

import com.google.gson.Gson

class Error (val code:Int,val description:String) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}