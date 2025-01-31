package wiles.shared.constants

import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.DIGIT_SEPARATOR
import wiles.shared.constants.Tokens.IDENTIFIER_START
import wiles.shared.constants.Tokens.KEYWORD_START
import wiles.shared.constants.Tokens.NUM_START
import wiles.shared.constants.Tokens.STRING_START

object Utils {
    @JvmStatic
    fun isAlphanumeric(c: Int): Boolean {
        return isAlphabetic(c) || isDigit(c)
    }

    @JvmStatic
    fun isAlphabetic(c: Int): Boolean {
        return Character.isAlphabetic(c) || c == DIGIT_SEPARATOR.code
                || (Character.isEmoji(c) && c > 127) || (c == 8205) || (c == 65039)
    }

    @JvmStatic
    fun isDigit(c: Int): Boolean {
        return Character.isDigit(c) || c == DIGIT_SEPARATOR.code
    }

    @JvmStatic
    fun isWhitespace(c: Int): Boolean {
        return Character.isWhitespace(c) && (c != 8205)
    }

    val NULL_LOCATION = TokenLocation()

    private fun escapeName(name: String, isToken: Boolean) : String
    {
        if(name.startsWith(IDENTIFIER_START) || name.startsWith(NUM_START)) return name
        if(!name.startsWith(STRING_START)) {
            if(isToken) return KEYWORD_START + name
            return name
        }
        var stringValue = name.substring(1)
        stringValue = stringValue.replace("\\","\\\\")
        stringValue = stringValue.replace("'","\\'")
        return "'$stringValue'"
    }

    fun <T> statementToString(name : String, type : SyntaxType, components : List<T>) : String
    {
        val hasComponents = components.isNotEmpty()
        val isToken = (type == SyntaxType.TOKEN)
        val hasName = name.isNotEmpty()
        return ((if(!isToken) "$type" else "") +
                (if(hasName) " " else "") +
                escapeName(name, isToken)+
                (if(hasName) " " else "") +
                (if(hasComponents) "(" else "") +
                (if(!hasComponents) "" else components.joinToString(", ")+")")).trim()
    }
}