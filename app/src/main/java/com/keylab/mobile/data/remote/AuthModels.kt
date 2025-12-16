package com.keylab.mobile.data.remote

import com.google.gson.annotations.SerializedName

data class SupabaseAuthRequest(
    @SerializedName("id_token") val idToken: String,
    @SerializedName("provider") val provider: String = "google"
)

data class SupabaseAuthResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Int?,
    @SerializedName("user") val user: UserDto?
)

data class UserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("user_metadata") val userMetadata: UserMetadata?
)

data class UserMetadata(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("name") val name: String?, // A veces viene como 'name'
    @SerializedName("picture") val picture: String? // A veces viene como 'picture'
)
