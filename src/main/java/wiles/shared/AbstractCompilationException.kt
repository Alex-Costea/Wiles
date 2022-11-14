package wiles.shared

abstract class AbstractCompilationException(s: String, @JvmField val tokenLocation: TokenLocation) : Exception(s) {
    @JvmName("getTokenLocationNullable")
    fun getTokenLocation(): TokenLocation {
        return tokenLocation
    }
}