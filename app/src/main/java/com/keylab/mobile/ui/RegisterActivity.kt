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
import com.keylab.mobile.databinding.ActivityRegisterBinding
import com.keylab.mobile.domain.model.Usuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.registerButton.setOnClickListener {
            attemptRegister()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun attemptRegister() {
        val nombre = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()
        val termsAccepted = binding.termsCheckbox.isChecked

        binding.nameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.confirmPasswordInputLayout.error = null

        var isValid = true

        if (nombre.isEmpty()) {
            binding.nameInputLayout.error = getString(R.string.error_empty_name)
            isValid = false
        }

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

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            binding.confirmPasswordInputLayout.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        }

        if (!termsAccepted) {
            Toast.makeText(this, getString(R.string.error_terms_not_accepted), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (isValid) {
            performRegister(nombre, email, password)
        }
    }

    private fun performRegister(nombre: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.registerButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val emailExists = database.usuarioDao().emailExiste(email).first()
                
                if (emailExists) {
                    binding.progressBar.visibility = View.GONE
                    binding.registerButton.isEnabled = true
                    binding.emailInputLayout.error = getString(R.string.error_email_already_exists)
                    return@launch
                }

                val usuario = Usuario(
                    nombre = nombre,
                    email = email,
                    password = password
                )

                val userId = database.usuarioDao().insertar(usuario)

                preferencesManager.guardarSesion(userId.toInt())

                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.success_account_created),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.registerButton.isEnabled = true
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.error_registration_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
