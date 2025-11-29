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
    private const val AUTH_URL = "${BuildConfig.SUPABASE_URL}/auth/v1/"
    
    private var preferencesManager: com.keylab.mobile.data.local.PreferencesManager? = null

    fun init(context: android.content.Context) {
        preferencesManager = com.keylab.mobile.data.local.PreferencesManager(context)
    }

    // Interceptor para agregar headers de autenticación Supabase
    private val authInterceptor = Interceptor { chain ->
        val token = preferencesManager?.obtenerAccessToken() ?: BuildConfig.SUPABASE_KEY
        
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()
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

    // Instancia Retrofit para Auth (Lazy)
    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(okHttpClient) // Reusing client, but auth endpoints usually need anon key, handled by interceptor logic?
            // Wait, auth endpoints like /token need apikey, but Authorization header should be Bearer <anon_key> usually for login.
            // My interceptor sends user token if logged in. This might be an issue for /token if user is already logged in?
            // Actually for /token (login), we don't have a user token yet, so it uses anon key. Correct.
            // For /recover, same.
            // For /user (get user), we need user token.
            // So the logic `token = prefs.token ?: anon_key` works for login (prefs empty) and authenticated requests (prefs has token).
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // ApiService público para usar en Repository
    val apiService: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }

    // AuthService público
    val authService: SupabaseAuthService by lazy {
        authRetrofit.create(SupabaseAuthService::class.java)
    }
}
