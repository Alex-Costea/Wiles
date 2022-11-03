package wiles.parser.exceptions

import wiles.parser.data.TokenLocation

abstract class AbstractCompilationException(s: String, @JvmField val tokenLocation: TokenLocation) : Exception(s) {
    @JvmName("getTokenLocationNullable")
    fun getTokenLocation(): TokenLocation {
        return tokenLocation
    }
}