package wiles.shared

import wiles.shared.constants.ErrorMessages.LINE_SYMBOL
import kotlin.math.min

class TokenLocation(val line: Int,
                    val lineIndex: Int,
                    val lineEnd : Int,
                    val lineEndIndex : Int,
    ) {
    constructor() : this(-1,-1, -1, -1)

    override fun equals(other: Any?): Boolean {
        if (other is TokenLocation)
            return line == other.line && lineIndex == other.lineIndex
        return false
    }

    fun displayLocation(input: String, additionalLines: Int): String
    {
        val string = input.split("\n")[line+additionalLines-1] + " "
        var nrCarats = string.length - lineIndex + 1
        if(line == lineEnd)
            nrCarats = min(nrCarats, lineEndIndex - lineIndex)
        return  LINE_SYMBOL + string +
                LINE_SYMBOL + " ".repeat(lineIndex-1) + "^".repeat(nrCarats) + LINE_SYMBOL
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineIndex
        return result
    }

    override fun toString(): String {
        return "TokenLocation(line=$line, lineIndex=$lineIndex, lineEnd=$lineEnd, lineEndIndex=$lineEndIndex)"
    }
}
