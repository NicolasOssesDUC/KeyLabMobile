package com.keylab.mobile.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // nombres de las claves
    companion object {
        private const val PREFS_NAME = "KeyLab_preferences"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    //instancia

    fun guardarSesion(userId: Int) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun obtenerUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun cerrarSesion() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

}
