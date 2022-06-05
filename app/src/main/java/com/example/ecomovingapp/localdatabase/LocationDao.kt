package com.example.ecomovingapp.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Query("select * from location")
    fun getAll():List<Location>

    @Insert
    fun insertOne(location:Location)

    @Query("DELETE FROM location")
    fun emptyDatabase()

}