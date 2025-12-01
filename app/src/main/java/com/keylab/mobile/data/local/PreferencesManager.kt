package com.keylab.mobile.data.local
//se usa para guardar datos pequeños y simples en el dispositivo
import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)//mode private solo esta app puede acceder

    // nombres de las claves
    companion object {
        private const val PREFS_NAME = "KeyLab_preferences"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    //instancia

    fun guardarSesion(userId: Int, isAdmin: Boolean = false) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_ADMIN, isAdmin)
            apply()
        }
    }

    fun obtenerUserId(): Int {    //lee id guardado -1 es nadie a entrado/vacio
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) //La app usa esto al iniciarse. Si devuelve true, se salta la pantalla de Login. Si devuelve false, te muestra el Login.
    }
    
    fun esAdmin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
    }

    fun cerrarSesion() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID)     // borra los datos al iniciar sesion
            remove(KEY_IS_ADMIN)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

}
