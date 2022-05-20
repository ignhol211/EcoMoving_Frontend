package com.example.ecomovingapp

import com.google.gson.Gson

class Error (val code:Int,val description:String) {
    companion object{
        val error1 = Error(1,"Fail to connect")
        val error2 = Error(2,"Invalid user or password")
        val error3 = Error(2,"Not registered user")
    }
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}