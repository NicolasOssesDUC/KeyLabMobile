package com.keylab.mobile.utils

object ValidationUtils {
    
    // Regex simple para email (para no depender de android.util.Patterns en tests unitarios puros)
    private val EMAIL_PATTERN = Regex(
        """[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\.+[a-zA-Z]+"""
    )

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_PATTERN.matches(email)
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
