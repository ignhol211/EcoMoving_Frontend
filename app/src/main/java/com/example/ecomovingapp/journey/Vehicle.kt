package com.serverecomoving

import com.google.gson.Gson

class Vehicle(val latitude:Double, val longitude:Double) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}