package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

abstract class AbstractCompilationException : Exception {

    @JvmField
    val tokenLocation: TokenLocation?

    constructor(s: String, tokenLocation: TokenLocation?) :super(s) {
        this.tokenLocation = tokenLocation
    }

    constructor(s: String) : super(s) {
        tokenLocation = null
    }

    @JvmName("getTokenLocationNullable")
    fun getTokenLocation(): TokenLocation? {
        return tokenLocation
    }
}