package `in`.costea.wiles.constants

import `in`.costea.wiles.constants.Chars.DIGIT_SEPARATOR
import `in`.costea.wiles.data.TokenLocation

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

    val nullLocation = TokenLocation(-1,-1)
}