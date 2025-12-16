package com.keylab.mobile.data.remote

import com.keylab.mobile.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton para configuración de Retrofit + OkHttp
 * Maneja conexión con Supabase REST API
 */
object RetrofitClient {
    
    // Base URL de Supabase (desde BuildConfig)
    private const val BASE_URL = "${BuildConfig.SUPABASE_URL}/rest/v1/"
    
    // Interceptor para agregar headers de autenticación Supabase
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
            
        // Solo agregar Content-Type json si no existe otro (ej: multipart)
        if (originalRequest.header("Content-Type") == null && originalRequest.body?.contentType() == null) {
             builder.addHeader("Content-Type", "application/json")
        }
            
        val request = builder.build()
        chain.proceed(request)
    }
    
    // Interceptor para logs (solo en debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    // Cliente OkHttp con interceptores y timeouts
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Instancia Retrofit (Lazy)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // ApiService público para usar en Repository
    val apiService: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }
}
