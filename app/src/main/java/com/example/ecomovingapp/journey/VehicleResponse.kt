package com.example.ecomovingapp.journey

import com.google.gson.Gson
import com.serverecomoving.Vehicle

data class VehicleResponse (val vehicles:List<Vehicle>) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}