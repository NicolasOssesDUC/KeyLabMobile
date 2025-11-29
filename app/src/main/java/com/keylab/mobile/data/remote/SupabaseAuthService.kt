package com.keylab.mobile.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseAuthService {

    @POST("token")
    suspend fun signInWithPassword(
        @Query("grant_type") grantType: String = "password",
        @Body credentials: LoginRequest
    ): Response<AuthResponse>

    @POST("token")
    suspend fun signInWithIdToken(
        @Query("grant_type") grantType: String = "id_token",
        @Body request: GoogleLoginRequest
    ): Response<AuthResponse>

    @POST("recover")
    suspend fun recoverPassword(
        @Body request: RecoverPasswordRequest
    ): Response<Unit>
    
    @POST("signup")
    suspend fun signUp(
        @Body credentials: LoginRequest
    ): Response<AuthResponse>
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    @SerializedName("id_token") val idToken: String,
    val provider: String = "google"
)

data class RecoverPasswordRequest(
    val email: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user") val user: User
)

data class User(
    val id: String,
    val email: String?
)
