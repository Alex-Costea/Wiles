package `in`.costea.wiles.data

data class TokenLocation(val line : Int, val lineIndex : Int) {
    override fun equals(other : Any?): Boolean {
        if (other is TokenLocation)
            return line == other.line || lineIndex == other.lineIndex
        return false
    }

    override fun toString(): String = "Line $line, character $lineIndex: "

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineIndex
        return result
    }
}
