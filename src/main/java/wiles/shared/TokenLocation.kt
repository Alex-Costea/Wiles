package wiles.shared

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.constants.ErrorMessages.LINE_SYMBOL

//Suppress warning because is required for Jackson
@Suppress("CanBePrimaryConstructorProperty")
class TokenLocation(line: Int, lineIndex: Int) {
    constructor() : this(-1,-1)

    @JsonProperty("line")
    val line = line

    @JsonProperty("lineIndex")
    val lineIndex = lineIndex

    override fun equals(other: Any?): Boolean {
        if (other is TokenLocation)
            return line == other.line && lineIndex == other.lineIndex
        return false
    }

    fun displayLocation(input: String, resourceLineLength: Int): String
    {
        return  LINE_SYMBOL + input.split("\n")[line+resourceLineLength-1] +
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
