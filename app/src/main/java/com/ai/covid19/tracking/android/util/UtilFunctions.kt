package com.ai.covid19.tracking.android.util

fun String.hasValidPasswordFormat(): Boolean {
    val hasLowerCase = "(?=.*[a-z])"
    val hasUpperCase = "(?=.*[A-Z])"
    val hasDigit = "(?=.*[0-9])"
    val atLeastEightCharsLong = "(?=.{8,})"

    val validFormatRegex = "^$hasLowerCase$hasUpperCase$hasDigit$atLeastEightCharsLong".toRegex()

    return validFormatRegex.containsMatchIn(this)
}
