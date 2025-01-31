package wiles.shared.constants

import wiles.parser.statements.AbstractStatement
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
        stringValue = stringValue.replace("\\","\\b")
        stringValue = stringValue.replace("'","\\q")
        stringValue = stringValue.replace("\n","\\n")
        stringValue = stringValue.replace(" ","\\s")
        return "'$stringValue'"
    }

    fun statementToString(statement: AbstractStatement) : String
    {
        val components = statement.getComponents()
        val hasComponents = components.isNotEmpty()
        val name = statement.name
        val type = statement.syntaxType
        val isToken = (type == SyntaxType.TOKEN)
        val hasName = name.isNotEmpty()
        val componentStrings = statement.getComponents().map { "\n$it".replace("\n","\n    ") }
        return ((if(!isToken) "$type" else "") +
                (if(hasName && !isToken) ": " else "") +
                escapeName(name, isToken) +
                (if(hasName && !isToken) " " else "") +
                (if(hasComponents) "\n(" else "") +
                (if(!hasComponents) "" else componentStrings.joinToString(", ")
                        + "\n)"))
    }
}