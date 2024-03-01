package wiles.shared.constants

import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.DIGIT_SEPARATOR

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

    fun <T> statementToString(name : String, type : SyntaxType, components : List<T>) : String
    {
        val hasComponents = components.isNotEmpty()
        val isToken = (type == SyntaxType.TOKEN)
        val hasName = name.isNotEmpty()
        return ((if(!isToken) "$type" else "") +
                (if(hasName) " " else "") +
                (name + (if(hasComponents && name.isNotEmpty()) "; " else ""))+
                (if(hasComponents) "(" else "") +
                (if(!hasComponents) "" else components.joinToString("; ")+")")).trim()
    }

    fun createFunctionType(variableType: Pair<JSONStatement, JSONStatement>): JSONStatement
    {
        return JSONStatement(syntaxType = SyntaxType.TYPE, name = Tokens.METHOD_ID,
            components = mutableListOf(
                JSONStatement(syntaxType = SyntaxType.METHOD,
                    components = mutableListOf(
                        variableType.second,
                        JSONStatement(syntaxType = SyntaxType.DECLARATION, name = Tokens.ANON_ARG_ID,
                            components = mutableListOf(variableType.first,
                                JSONStatement(syntaxType = SyntaxType.TOKEN, name = "!elem")
                            )),
                    ))
            ))
    }
}