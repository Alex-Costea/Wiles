package wiles.parser.converters

import org.unbescape.html.HtmlEscape
import wiles.parser.exceptions.StringInvalidException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars.COMMENT_START
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.Chars.STRING_DELIMITER
import wiles.shared.constants.ErrorMessages
import wiles.shared.constants.Tokens
import wiles.shared.constants.Utils.isAlphabetic
import wiles.shared.constants.Utils.isAlphanumeric
import wiles.shared.constants.Utils.isDigit
import wiles.shared.constants.Utils.isWhitespace
import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

class InputToTokensConverter(input: String, private val lastLocation: TokenLocation) {
    private val arrayChars: IntArray = input.codePoints().toArray()
    private val exceptions = CompilationExceptionsCollection()
    private var originalIndex = 0
    private var index = 0
    private var lineIndex = -1 //character at index -1 can be considered equivalent to newline
    private var line = 1

    fun convert(): List<Token> {
        val tokens = ArrayList<Token>()
        index = 0
        while (index < arrayChars.size) {
            try {
                originalIndex = index
                if (arrayChars[index] == STRING_DELIMITER.code)  //string literal
                {
                    val string = readStringLiteral()
                    val unescaped = unescape(string)
                    tokens.add(createToken(unescaped))
                } else if (isAlphabetic(arrayChars[index]))  //identifier
                {
                    tokens.add(createToken(readIdentifier()))
                } else if (isDigit(arrayChars[index]))  //numeral literal
                {
                    tokens.add(createToken(readNumeralLiteral()))
                } else if (arrayChars[index] == COMMENT_START.code)  //operator
                {
                    createComment()
                } else {
                    val id = readSymbol()
                    val size = tokens.size
                    if (size > 0 && id == Tokens.NEWLINE_ID && tokens[size - 1].content == Tokens.CONTINUE_LINE_ID) {
                        tokens.removeAt(size - 1)
                        addNewLine()
                        index++
                        continue  // backslash followed by newline is ignored
                    }
                    if (id.isNotBlank()) tokens.add(createToken(id))
                    else tokens.add(createToken(""))
                    if (id == Tokens.NEWLINE_ID) addNewLine()
                }
            } catch (ex: AbstractCompilationException) {
                exceptions.add(ex)
                tokens.add(createToken(Tokens.ERROR_TOKEN))
            }
            index++
        }
        return removeNull(addLocationEnd(tokens))
    }

    private fun addLocationEnd(tokens: ArrayList<Token>): ArrayList<Token> {
        val newTokens = ArrayList<Token>()
        for (i in tokens.indices) {
            val token = tokens[i]
            val nextLocation = if (i != tokens.size - 1) {
                tokens[i + 1].location
            } else {
                lastLocation
            }
            val location = token.location
            newTokens.add(
                Token(
                    token.content,
                    TokenLocation(
                        location.line, location.lineIndex,
                        nextLocation.line, nextLocation.lineIndex
                    )
                )
            )
        }
        return newTokens
    }

    private fun removeNull(tokens: ArrayList<Token>): List<Token> {
        return tokens.stream().filter { token: Token -> token.content.isNotEmpty() }.toList()
    }

    private fun unescapeGroup(match: String): String {
        val newMatch = if (match[match.length - 1] == ';') match else "$match;"
        if (ESCAPE_SEQUENCES.containsKey(newMatch)) return ESCAPE_SEQUENCES[newMatch]!!
        val beforeEscape = "&" + newMatch.substring(1)
        val htmlMatch = HtmlEscape.unescapeHtml("&" + newMatch.substring(1))
        if (htmlMatch == beforeEscape) return match
        return htmlMatch
    }

    private fun unescape(s: String): String {
        val pattern = Pattern.compile("\\\\#?\\w+;|\\\\\\w")
        val matcher = pattern.matcher(s)
        return matcher.replaceAll((Function {
            Matcher.quoteReplacement(
                unescapeGroup(
                    matcher.group()
                )
            )
        }))
    }

    private fun createString(): StringBuilder {
        var currentIndex = index + 1
        val sb = StringBuilder()
        while (currentIndex < arrayChars.size) {
            if (arrayChars[currentIndex] == STRING_DELIMITER.code) break
            sb.appendCodePoint(arrayChars[currentIndex])
            if (currentIndex + 1 == arrayChars.size) break
            currentIndex++
        }
        index = currentIndex
        return sb
    }

    private fun createComment() {
        var currentIndex = index + 1
        while (currentIndex < arrayChars.size) {
            if (arrayChars[currentIndex] == '\n'.code) {
                currentIndex--
                break
            }
            if (currentIndex + 1 == arrayChars.size) break
            currentIndex++
        }
        index = currentIndex
    }

    @Throws(StringInvalidException::class)
    private fun readStringLiteral(): String {
        if (index >= arrayChars.size) throw StringInvalidException(
            ErrorMessages.STRING_UNFINISHED_ERROR,
            TokenLocation(line, indexOnCurrentLine, -1, -1)
        )
        val sb = createString()
        if (index < arrayChars.size && arrayChars[index] == STRING_DELIMITER.code) return Tokens.STRING_START + sb

        //String not properly finished at this point
        if (index < arrayChars.size && arrayChars[index] == '\n'.code)  //of the newline token regardless
            index--
        throw StringInvalidException(
            ErrorMessages.STRING_UNFINISHED_ERROR,
            TokenLocation(line, indexOnCurrentLine, -1, -1)
        )
    }

    private fun readIdentifier(): String {
        var currentIndex = index
        val sb = StringBuilder()
        while (currentIndex < arrayChars.size && isAlphanumeric(arrayChars[currentIndex])) {
            sb.appendCodePoint(arrayChars[currentIndex])
            currentIndex++
        }
        index = currentIndex - 1
        return Tokens.TOKENS.getOrDefault(sb.toString(), Tokens.IDENTIFIER_START + sb)
    }

    private fun readNumeralLiteral(): String {
        var currentIndex = index
        val sb = StringBuilder(Tokens.NUM_START)
        var delimiterAlreadyFound = false
        while (currentIndex < arrayChars.size && (isDigit(arrayChars[currentIndex]) ||  //first delimiter found, and not as the last digit
                    (!delimiterAlreadyFound && arrayChars[currentIndex] == DECIMAL_DELIMITER.code && currentIndex + 1 < arrayChars.size && isDigit(
                        arrayChars[currentIndex + 1]
                    )))
        ) {
            sb.appendCodePoint(arrayChars[currentIndex])
            if (arrayChars[currentIndex] == DECIMAL_DELIMITER.code) delimiterAlreadyFound = true
            currentIndex++
        }
        index = currentIndex - 1
        return sb.toString()
    }

    private fun readSymbol(): String {
        var currentIndex = index
        var operatorFoundIndex = index
        val sb = StringBuilder()
        var token: String? = null
        while (!isAlphanumeric(arrayChars[currentIndex]) && currentIndex - index < Tokens.MAX_SYMBOL_LENGTH) {
            sb.appendCodePoint(arrayChars[currentIndex])
            val tempId = Tokens.TOKENS[sb.toString()]
            if (tempId != null) {
                token = tempId
                operatorFoundIndex = currentIndex
            }
            currentIndex++
            if (isWhitespace(arrayChars[currentIndex - 1]) || currentIndex == arrayChars.size || arrayChars[currentIndex] == '\n'.code) break
        }
        index = operatorFoundIndex
        if (token == null) {
            index = currentIndex - 1
            token = sb.toString()
        }
        return token
    }

    private fun createToken(token: String): Token {
        return Token(token, TokenLocation(line, indexOnCurrentLine, -1, -1))
    }

    private fun addNewLine() {
        line++
        lineIndex = index
    }

    private val indexOnCurrentLine: Int
        get() = originalIndex - lineIndex


    @Throws(AbstractCompilationException::class)
    fun throwExceptionIfExists(exceptionIndex: Int) {
        if (exceptions.size > exceptionIndex) throw exceptions[exceptionIndex]
    }

    fun getExceptions(): CompilationExceptionsCollection {
        return exceptions.clone() as CompilationExceptionsCollection
    }

    companion object {
        private val ESCAPE_SEQUENCES = HashMap<String, String>()

        init {
            ESCAPE_SEQUENCES["\\q;"] = "\""
            ESCAPE_SEQUENCES["\\n;"] = "\n"
            ESCAPE_SEQUENCES["\\b;"] = "\\"
            ESCAPE_SEQUENCES["\\s;"] = ";"
            ESCAPE_SEQUENCES["\\w;"] = " "
            ESCAPE_SEQUENCES["\\a;"] = "'"
        }
    }
}
