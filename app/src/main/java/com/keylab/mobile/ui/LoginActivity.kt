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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)

        checkExistingSession()
        setupListeners()
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
            Toast.makeText(this, "Inicio con Google próximamente", Toast.LENGTH_SHORT).show()
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
    
    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
