package com.keylab.mobile.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    // PRUEBA 1: Correos válidos estándar
    // Verifica que formatos comunes de correo sean aceptados.
    @Test
    fun `isValidEmail returns true for valid emails`() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidEmail("nombre.apellido@empresa.co"))
        assertTrue(ValidationUtils.isValidEmail("admin@keylab.com"))
    }

    // PRUEBA 2: Correos inválidos básicos
    // Verifica el rechazo de formatos incorrectos (sin arroba, vacíos, sin dominio).
    @Test
    fun `isValidEmail returns false for invalid emails`() {
        assertFalse(ValidationUtils.isValidEmail("sinarroba.com"))
        assertFalse(ValidationUtils.isValidEmail("test@"))
        assertFalse(ValidationUtils.isValidEmail("@dominio.com"))
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("   "))
    }

    // PRUEBA 3: Contraseña válida (Longitud correcta)
    // Verifica que contraseñas con 6 o más caracteres sean aceptadas.
    @Test
    fun `isValidPassword returns true for long passwords`() {
        assertTrue(ValidationUtils.isValidPassword("123456"))
        assertTrue(ValidationUtils.isValidPassword("passwordSeguro"))
    }

    // PRUEBA 4: Contraseña inválida (Muy corta)
    // Verifica que contraseñas con menos de 6 caracteres sean rechazadas.
    @Test
    fun `isValidPassword returns false for short passwords`() {
        assertFalse(ValidationUtils.isValidPassword("12345"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }

    // PRUEBA 5: Correos con Mayúsculas y Minúsculas
    // Verifica que el validador no sea sensible a mayúsculas (debe aceptarlas).
    @Test
    fun `isValidEmail returns true for mixed case emails`() {
        assertTrue(ValidationUtils.isValidEmail("Test.User@Example.Com"))
    }

    // PRUEBA 6: Dominios con números
    // Verifica que se acepten dominios que contienen números (ej: 123host.com).
    @Test
    fun `isValidEmail returns true for emails with numeric domain`() {
        assertTrue(ValidationUtils.isValidEmail("user@123host.com"))
    }

    // PRUEBA 7: Doble arroba
    // Verifica que se rechacen correos con más de un símbolo '@'.
    @Test
    fun `isValidEmail returns false for double at sign`() {
        assertFalse(ValidationUtils.isValidEmail("user@@example.com"))
        assertFalse(ValidationUtils.isValidEmail("user@sub@domain.com"))
    }

    // PRUEBA 8: Dominio incompleto
    // Verifica que se rechacen correos sin extensión de dominio (ej: .com, .net).
    @Test
    fun `isValidEmail returns false for missing top level domain`() {
        assertFalse(ValidationUtils.isValidEmail("user@localhost")) // Regex pide .algo
        assertFalse(ValidationUtils.isValidEmail("user@com"))
    }

    // PRUEBA 9: Contraseña con espacios
    // Verifica que los espacios cuenten como caracteres válidos en la contraseña.
    @Test
    fun `isValidPassword returns true for password with spaces`() {
        assertTrue(ValidationUtils.isValidPassword("pass word")) 
    }

    // PRUEBA 10: Contraseña muy larga
    // Verifica que no haya límite superior arbitrario (o que acepte contraseñas largas).
    @Test
    fun `isValidPassword returns true for very long passwords`() {
        val longPass = "a".repeat(100)
        assertTrue(ValidationUtils.isValidPassword(longPass))
    }
}