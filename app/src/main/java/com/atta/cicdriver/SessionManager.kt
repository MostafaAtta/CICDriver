package com.atta.cicdriver

import android.content.Context
import android.content.SharedPreferences

class SessionManager {

    private val KEY_USERNAME = "username"
    private val KEY_USER_ID = "user_id"
    private val KEY_EMAIL = "email"
    private val KEY_PHONE = "phone"
    private val KEY_CITY = "city"
    private val KEY_IS_LOGIN = "is_login"


    companion object{
        private const val PREF_NAME = "med_pref"
        //mode 0 private
        private const val MODE = 0
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_ROUTE_NAME = "route_name"
        private const val KEY_ROUTE_ID = "route_id"

        private var singleton: SessionManager? = null
        private lateinit var pref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor

        fun with(context: Context) : SessionManager {
            if (null == singleton)
                singleton = Builder(context).build()
            return singleton as SessionManager
        }
    }
    constructor()

    constructor(context: Context) {
        pref =  context.getSharedPreferences(PREF_NAME, MODE)
        editor = pref.edit()
    }

    fun login(user: User){
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_FIRST_NAME, user.firstName)
        editor.putString(KEY_LAST_NAME, user.lastName)
        editor.putString(KEY_PHONE, user.phone)
        editor.putString(KEY_USER_ID, user.id)

        editor.apply()

    }

    fun saveRouteName(routeName: String){
        editor.putString(KEY_ROUTE_NAME, routeName)

        editor.apply()

    }

    fun getRouteName(): String{

        return pref.getString(KEY_ROUTE_NAME, "").toString()
    }

    fun getUserId(): String{

        return pref.getString(KEY_USER_ID, "").toString()
    }

    fun saveRouteId(id: String) {
        editor.putString(KEY_ROUTE_ID, id)

        editor.apply()
    }


    fun getRouteId(): String{

        return pref.getString(KEY_ROUTE_ID, "").toString()
    }

    private class Builder(val context: Context) {

        fun build() : SessionManager {

            return SessionManager(context)
        }
    }

}