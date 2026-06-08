package com.dexter.app.presentation.ui.utils

object InputFilters {

    // Letras (incluyendo acentos/ñ), espacios, guiones — para nombre y país
    private val NAME_REGEX = Regex("^[\\p{L}\\s\\-'.]*$")

    // Teléfono: dígitos, +, -, (), espacios
    private val PHONE_REGEX = Regex("^[+\\d\\s()\\-]*$")

    // Hora HH:MM
    private val TIME_REGEX = Regex("^\\d{0,2}:?\\d{0,2}$")

    // Fecha DD/MM/YYYY o YYYY-MM-DD
    private val DATE_REGEX = Regex("^[\\d\\-/]*$")

    // Texto general sin caracteres peligrosos
    private val SAFE_TEXT_REGEX = Regex("^[^<>{}\\[\\]\\\\/]*$")

    // Solo dígitos
    private val DIGITS_ONLY_REGEX = Regex("^\\d*$")

    fun filterName(input: String): String =
        if (NAME_REGEX.matches(input)) input else input.filter { c ->
            c.isLetter() || c.isWhitespace() || c == '-' || c == '\'' || c == '.'
        }

    fun filterPhone(input: String): String =
        input.filter { c -> c.isDigit() || c == '+' || c == '-' || c == '(' || c == ')' || c == ' ' }

    fun filterSafeText(input: String): String =
        input.filter { c -> c !in "<>{}[]\\/" }

    fun filterTime(input: String): String =
        input.filter { c -> c.isDigit() || c == ':' }

    fun filterDate(input: String): String =
        input.filter { c -> c.isDigit() || c == '-' || c == '/' }

    fun filterDigits(input: String): String =
        input.filter { c -> c.isDigit() }

    fun filterSkill(input: String): String =
        input.filter { c -> c.isLetter() || c.isWhitespace() || c == '-' || c == '\'' }

    // Validaciones de negocio
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun isValidPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length in 7..15
    }

    fun isValidDuration(duration: String): Boolean {
        val d = duration.toIntOrNull() ?: return false
        return d in 15..480
    }

    fun isValidTime(time: String): Boolean =
        Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(time)

    fun isValidName(name: String): Boolean =
        name.trim().length >= 2

    fun isValidPassword(password: String): Boolean =
        password.length >= 6
}
