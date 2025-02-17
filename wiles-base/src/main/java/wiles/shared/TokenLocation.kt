package wiles.shared

import wiles.shared.constants.ErrorMessages.LINE_SYMBOL
import kotlin.math.min

class TokenLocation(val line: Int,
                    val lineIndex: Int,
                    val lineEnd : Int,
                    val lineEndIndex : Int,
    ) {
    constructor() : this(-1,-1, -1, -1)

    fun displayLocation(input: String): String
    {
        val string = input.split("\n")[line-1] + " "
        var nrCarats = string.length - lineIndex + 1
        if(line == lineEnd)
            nrCarats = min(nrCarats, lineEndIndex - lineIndex)
        return  LINE_SYMBOL + string +
                LINE_SYMBOL + " ".repeat(lineIndex-1) + "^".repeat(nrCarats) + LINE_SYMBOL
    }

    override fun toString(): String {
        return "TokenLocation(line=$line, lineIndex=$lineIndex, lineEnd=$lineEnd, lineEndIndex=$lineEndIndex)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TokenLocation

        if (line != other.line) return false
        if (lineIndex != other.lineIndex) return false
        if (lineEnd != other.lineEnd) return false
        if (lineEndIndex != other.lineEndIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineIndex
        result = 31 * result + lineEnd
        result = 31 * result + lineEndIndex
        return result
    }
}
