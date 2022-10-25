package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

abstract class AbstractCompilationException(s: String, @JvmField val tokenLocation: TokenLocation) : Exception(s) {
    @JvmName("getTokenLocationNullable")
    fun getTokenLocation(): TokenLocation {
        return tokenLocation
    }
}