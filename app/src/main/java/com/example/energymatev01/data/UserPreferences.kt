package com.example.energymatev01.data

import android.content.Context
import android.content.SharedPreferences
import com.example.energymatev01.ChangePassword

class UserPreferences(context: Context) {

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    fun isLoggedIn(bool: Boolean) {
        pref.getBoolean("isLoggedIn", false)
    }

    fun setLoggedIn(loggedIn: Boolean){
        pref.edit().putBoolean("isLoggedIn", loggedIn).apply()
    }

    fun saveUser(
        name: String,
        email: String,
        password: String,
        address: String,
        mobileNumber: String
    ){
        pref.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .putString("address", address)
            .putString("mobileNumber", mobileNumber)
            .apply()
    }

    fun savePassword(password: String){
        pref.edit().putString("password", password).apply()
    }

    fun getName() : String{
        return pref.getString("name", "") ?: ""
    }

    fun getEmail() : String{
        return pref.getString("email", "") ?: ""
    }

    fun getPassword() : String{
        return pref.getString("password", "") ?: ""
    }

    fun getAddress() : String{
        return pref.getString("address", "") ?: ""
    }

    fun getMobileNumber() : String{
        return pref.getString("mobileNumber", "") ?: ""
    }

    fun clearAll(){
        pref.edit().clear().apply()
    }

}