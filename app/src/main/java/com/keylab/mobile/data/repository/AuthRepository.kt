package com.keylab.mobile.data.repository

import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.data.remote.ApiResponse
import com.keylab.mobile.data.remote.GoogleLoginRequest
import com.keylab.mobile.data.remote.LoginRequest
import com.keylab.mobile.data.remote.RecoverPasswordRequest
import com.keylab.mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val preferencesManager: PreferencesManager) {

    private val authService = RetrofitClient.authService

    suspend fun login(email: String, password: String): ApiResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.signInWithPassword(
                    credentials = LoginRequest(email, password)
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    preferencesManager.guardarSesion(
                        userId = authResponse.user.id,
                        email = authResponse.user.email,
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    ApiResponse.Success(Unit)
                } else {
                    ApiResponse.Error("Error en inicio de sesión: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun loginWithGoogle(idToken: String): ApiResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.signInWithIdToken(
                    request = GoogleLoginRequest(idToken = idToken)
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    preferencesManager.guardarSesion(
                        userId = authResponse.user.id,
                        email = authResponse.user.email,
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    ApiResponse.Success(Unit)
                } else {
                    ApiResponse.Error("Error en inicio de sesión con Google: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun recoverPassword(email: String): ApiResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.recoverPassword(
                    request = RecoverPasswordRequest(email)
                )

                if (response.isSuccessful) {
                    ApiResponse.Success(Unit)
                } else {
                    ApiResponse.Error("Error al enviar correo de recuperación: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun signUp(nombre: String, email: String, password: String): ApiResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Supabase Auth signUp
                val response = authService.signUp(
                    credentials = LoginRequest(email, password)
                    // Note: Supabase allows sending metadata like 'full_name' in a separate object, 
                    // but for now we just register with email/password.
                    // If we want to save the name, we should send it in 'data' field or save it to a 'profiles' table.
                    // For simplicity, we just register.
                )

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    preferencesManager.guardarSesion(
                        userId = authResponse.user.id,
                        email = authResponse.user.email,
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    ApiResponse.Success(Unit)
                } else {
                    ApiResponse.Error("Error en registro: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error desconocido")
            }
        }
    }
    
    fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }
    
    fun logout() {
        preferencesManager.cerrarSesion()
    }
}
