package wiles.shared

import wiles.shared.constants.ErrorMessages.LINE_SYMBOL

class TokenLocation(val line: Int,val  lineIndex: Int) {
    constructor() : this(-1,-1)

    override fun equals(other: Any?): Boolean {
        if (other is TokenLocation)
            return line == other.line && lineIndex == other.lineIndex
        return false
    }

    fun displayLocation(input: String, additionalLines: Int): String
    {
        return  LINE_SYMBOL + input.split("\n")[line+additionalLines-1] +
                LINE_SYMBOL + " ".repeat(lineIndex-1) + "^"+ LINE_SYMBOL
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineIndex
        return result
    }

    override fun toString(): String {
        return "TokenLocation(line=$line, lineIndex=$lineIndex)"
    }
}
