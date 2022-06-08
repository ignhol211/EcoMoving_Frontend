package com.example.ecomovingapp.localdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity
data class Location (
    @PrimaryKey (autoGenerate = true) val id:Int,
    @ColumnInfo (name = "latitude") val latitude:Double,
    @ColumnInfo (name = "longitude") val longitude:Double,
    @ColumnInfo (name = "description") val description:String?
){

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}