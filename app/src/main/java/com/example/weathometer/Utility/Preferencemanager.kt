package com.example.weathometer.Utility

import android.content.Context
import android.content.SharedPreferences

class PreferenceMangager(context: Context) {
    private var sharedPreferences: SharedPreferences = context
        .getSharedPreferences(KEY_PERFERENCE_NAME, Context.MODE_PRIVATE)

    fun putBoolean(key : String, value : Boolean){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun getBoolean(key : String) : Boolean{
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key : String, value : String){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getString(key : String?) : String{
        return sharedPreferences.getString(key, "Barrie").toString()
    }

    fun putLong(key: String, value: Long){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(key,value)
        editor.apply()
    }

    fun getLong(key : String) : Long{
        return sharedPreferences.getLong(key, 0)
    }

    fun clear(){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


    companion object{
        const val KEY_PERFERENCE_NAME="weathometer_Preference"
        const val KEY_NOTIFICATION_ENABLED="notification_toggle"
        const val KEY_NOTIFICATION_TIME="notification_time"
        const val DEFAULT_CITY="default_city"



    }





}