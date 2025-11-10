package com.keylab.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.tvRegister.setOnClickListener {
            Toast.makeText(this, "Registro próximamente", Toast.LENGTH_SHORT).show()
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
            binding.emailInputLayout.error = "El correo electrónico es requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Correo electrónico inválido"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "La contraseña es requerida"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInputLayout.error = "Mínimo 6 caracteres"
            isValid = false
        }

        if (isValid) {
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false

        binding.root.postDelayed({
            binding.progressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true

            Toast.makeText(this, "¡Bienvenido a KeyLab!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}
