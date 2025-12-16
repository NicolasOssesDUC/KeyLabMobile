package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.databinding.ActivityLoginBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.result.contract.ActivityResultContracts
import com.keylab.mobile.data.remote.RetrofitClient
import com.keylab.mobile.data.remote.SupabaseAuthRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    // Launcher para el resultado de Google Sign In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    authenticateWithSupabase(idToken)
                } else {
                    Toast.makeText(this, "Error: No se obtuvo ID Token de Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In falló: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                android.util.Log.e("LoginActivity", "Google Sign In failed", e)
            }
        } else {
             android.util.Log.e("LoginActivity", "Google Sign In result code: ${result.resultCode}")
             Toast.makeText(this, "Inicio de sesión cancelado o configuración incorrecta", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)
        
        setupGoogleSignIn()

        checkExistingSession()
        setupListeners()
    }
    
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("68664819972-mgc5in5rtto4n1tgidn4a9eglgqqsg6j.apps.googleusercontent.com")
            .requestEmail()
            .build()
            
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun checkExistingSession() {
        if (preferencesManager.isLoggedIn()) {
            if (preferencesManager.esAdmin()) {
                navigateToAdminDashboard()
            } else {
                navigateToMain()
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        
        binding.googleLoginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
        
        binding.phoneLoginButton.setOnClickListener {
            Toast.makeText(this, "Inicio con teléfono próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun attemptLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null

        var isValid = true

        if (email.isEmpty()) {
            binding.emailInputLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        if (isValid) {
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val usuario = database.usuarioDao().validarLogin(email, password).first()

                binding.progressBar.visibility = View.GONE
                binding.loginButton.isEnabled = true

                if (usuario != null) {
                    val isAdmin = usuario.email.endsWith("@keylab.com")
                    preferencesManager.guardarSesion(usuario.id, isAdmin)
                    
                    Toast.makeText(
                        this@LoginActivity,
                        "¡Bienvenido ${usuario.nombre}!",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    if (isAdmin) {
                        navigateToAdminDashboard()
                    } else {
                        navigateToMain()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.error_login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.loginButton.isEnabled = true
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error_login_general),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun authenticateWithSupabase(idToken: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = SupabaseAuthRequest(idToken = idToken)
                // Usamos URL completa para Auth
                val authUrl = "${com.keylab.mobile.BuildConfig.SUPABASE_URL}/auth/v1/token"
                val response = RetrofitClient.apiService.signInWithIdToken(authUrl, request = request)
                
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    val user = authResponse?.user
                    
                    if (user != null && !user.email.isNullOrEmpty()) {
                        val email = user.email
                        // Generar ID numérico estable basado en email
                        val userId = kotlin.math.abs(email.hashCode())
                        val isAdmin = email.endsWith("@keylab.com")
                        
                        // Sincronizar con BD Local
                        val usuarioDao = database.usuarioDao()
                        val existeUsuario = usuarioDao.obtenerPorEmail(email).firstOrNull()
                        
                        // Obtener nombre y foto de forma robusta
                        val nombreDisplay = user.userMetadata?.fullName 
                            ?: user.userMetadata?.name 
                            ?: "Usuario Google"
                            
                        val avatarDisplay = user.userMetadata?.avatarUrl 
                            ?: user.userMetadata?.picture
                        
                        // Preparar usuario para insertar/actualizar
                        // Mantenemos password y fecha de registro si ya existía
                        val passwordActual = existeUsuario?.password ?: ""
                        val fechaRegistroActual = existeUsuario?.fechaRegistro ?: System.currentTimeMillis()
                        
                        val usuarioActualizado = com.keylab.mobile.domain.model.Usuario(
                            id = userId,
                            nombre = nombreDisplay,
                            email = email,
                            password = passwordActual,
                            fechaRegistro = fechaRegistroActual,
                            avatarUrl = avatarDisplay
                        )
                        
                        // Insertar o Reemplazar (OnConflictStrategy.REPLACE en DAO)
                        usuarioDao.insertar(usuarioActualizado)

                        // Guardar sesión y navegar
                        preferencesManager.guardarSesion(userId, isAdmin)
                        
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        Toast.makeText(this@LoginActivity, "¡Bienvenido $nombreDisplay!", Toast.LENGTH_SHORT).show()
                        
                        if (isAdmin) {
                            navigateToAdminDashboard()
                        } else {
                            navigateToMain()
                        }
                    } else {
                        // Respuesta exitosa pero sin usuario (raro en Auth)
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Error: Datos de usuario incompletos", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    android.util.Log.e("LoginGoogle", "Error Supabase: $errorBody")
                    Toast.makeText(this@LoginActivity, "Error al autenticar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.loginButton.isEnabled = true
                android.util.Log.e("LoginGoogle", "Excepción", e)
                Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
