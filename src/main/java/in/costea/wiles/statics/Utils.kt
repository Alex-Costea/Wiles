package `in`.costea.wiles.statics

import `in`.costea.wiles.statics.Constants.DIGIT_SEPARATOR

object Utils {
    @JvmStatic
    fun isAlphanumeric(c: Char): Boolean {
        return isAlphabetic(c) || isDigit(c)
    }

    @JvmStatic
    fun isAlphabetic(c: Char): Boolean {
        return Character.isAlphabetic(c.code) || c == DIGIT_SEPARATOR
    }

    @JvmStatic
    fun isDigit(c: Char): Boolean {
        return Character.isDigit(c)
    }

    @JvmStatic
    fun todo(s: String) {
        TODO(s)
    }
}