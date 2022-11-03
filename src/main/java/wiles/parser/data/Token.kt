package wiles.parser.data

data class Token(val content: String, val location: TokenLocation) {
    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Token
        if (content != other.content) return false
        if (location != other.location) return false
        return true
    }
}
