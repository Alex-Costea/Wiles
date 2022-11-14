package wiles.shared

import wiles.shared.constants.ErrorMessages.LINE_SYMBOL

data class TokenLocation(val line: Int, val lineIndex: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is TokenLocation)
            return line == other.line && lineIndex == other.lineIndex
        return false
    }

    fun displayLocation(input : String): String
    {
        return  LINE_SYMBOL + input.split("\n")[line-1] +
                LINE_SYMBOL + " ".repeat(lineIndex-1) + "^"+ LINE_SYMBOL
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineIndex
        return result
    }
}
