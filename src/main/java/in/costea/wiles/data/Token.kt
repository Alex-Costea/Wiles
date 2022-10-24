package `in`.costea.wiles.data

import `in`.costea.wiles.constants.ErrorMessages.AT_LINE_INDEX

data class Token(val content: String, val location: TokenLocation?) {

    override fun toString(): String {
        location ?: return ""
        return AT_LINE_INDEX.format(content,location.line,location.lineIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Token) return false
        if (content != other.content) return false
        location ?: return true
        other.location ?: return true
        return location == other.location
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

}
