package wiles.shared

abstract class WilesException(val s: String, @JvmField val tokenLocation: TokenLocation) : Exception(s) {
    @JvmName("getTokenLocationNullable")
    fun getTokenLocation(): TokenLocation {
        return tokenLocation
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WilesException

        if (s != other.s) return false
        if (tokenLocation != other.tokenLocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = s.hashCode()
        result = 31 * result + tokenLocation.hashCode()
        return result
    }


}