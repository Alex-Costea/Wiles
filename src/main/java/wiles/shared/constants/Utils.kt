package wiles.shared.constants

import wiles.shared.SyntaxType
import wiles.shared.constants.Chars.DIGIT_SEPARATOR
import wiles.shared.TokenLocation

object Utils {
    @JvmStatic
    fun isAlphanumeric(c: Char): Boolean {
        return isAlphabetic(c) || isDigit(c)
    }

    @JvmStatic
    fun isAlphabetic(c: Char): Boolean {
        return Character.isAlphabetic(c.code) || c == DIGIT_SEPARATOR
    }

    @JvmStatic
    fun isDigit(c: Char): Boolean {
        return Character.isDigit(c)
    }

    val nullLocation = TokenLocation(-1,-1)

    fun <T> statementToString(name : String, type : SyntaxType, components : List<T>) : String
    {
        val hasComponents = components.isNotEmpty()
        val isToken = (type == SyntaxType.TOKEN)
        return ((if(!isToken) "$type" else "") +
                (if(!hasComponents) " " else "") +
                (name + (if(hasComponents && name.isNotEmpty()) "$name; " else ""))+
                (if(hasComponents) "(" else "") +
                (if(!hasComponents) "" else components.joinToString("; ")+")")).trim()
    }
}