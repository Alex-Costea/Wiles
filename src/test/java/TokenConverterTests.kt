import wiles.shared.constants.ErrorMessages.STRING_UNFINISHED_ERROR
import wiles.shared.constants.Settings.DEBUG
import wiles.shared.constants.Settings.MAX_SYMBOL_LENGTH
import wiles.shared.constants.Tokens.ACCESS_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Utils.NULL_LOCATION
import wiles.parser.converters.InputToTokensConverter
import wiles.shared.Token
import wiles.shared.AbstractCompilationException
import wiles.parser.exceptions.StringUnfinishedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import wiles.shared.constants.Tokens.ELEM_ACCESS_ID
import java.util.*

class TokenConverterTests {

    private fun tokenConverterEquals(input: String, solution: Array<String>) {
        val solutionList: MutableList<Token> = ArrayList()
        for (s in solution) {
            solutionList.add(Token(s, NULL_LOCATION))
        }
        val givenList = InputToTokensConverter(input).convert()
        assert(givenList.size == solutionList.size)
        for((i,x) in givenList.withIndex())
            assert(x.content == solutionList[i].content)
    }

    private fun tokenConverterThrows(exceptionIndex: Int, input: String, throwing: Class<out Throwable>, message: String? = null, line: Int? = null) {
        val x = InputToTokensConverter(input)
        x.convert()
        val t = if (message != null) Assertions.assertThrows(throwing, { x.throwExceptionIfExists(exceptionIndex) }, message) else Assertions.assertThrows(throwing) { x.throwExceptionIfExists(exceptionIndex) }
        assert(t is AbstractCompilationException)
        if (line != null) Assertions.assertEquals(line, Objects.requireNonNull((t as AbstractCompilationException).getTokenLocation()).line)
    }

    @Test
    fun emptyInputsTest() {
        tokenConverterEquals("", arrayOf())
        tokenConverterEquals("     ", arrayOf())
    }

    @Test
    fun commentTest() {
        tokenConverterEquals("#", arrayOf())
        tokenConverterEquals("#\n", arrayOf("NEWLINE"))
        tokenConverterEquals("abc#de\nfgh", arrayOf("!abc", "NEWLINE", "!fgh"))
        tokenConverterEquals("abc#de\\   \nfgh", arrayOf("!abc"))
        tokenConverterEquals("abc#a b c d e f break end continue", arrayOf("!abc"))
    }

    @Test
    fun symbolsTest() {
        tokenConverterEquals("=/=", arrayOf("NOT_EQUAL"))
        tokenConverterEquals( "$", arrayOf("$"))
        tokenConverterEquals( "=$", arrayOf("EQUALS","$"))
        val invalidProgram = "\${}{}{}{}{}"
        Assumptions.assumingThat(invalidProgram.length >= 2 * MAX_SYMBOL_LENGTH + 1) {
            tokenConverterEquals( invalidProgram, arrayOf("\${}","{}{","}{}","{}"))
        }
        if (DEBUG) {
            tokenConverterEquals("$=", arrayOf("TEMP"))
            tokenConverterEquals("=$=", arrayOf("TEMP2"))
        }
        tokenConverterEquals( "$\n@", arrayOf("$","NEWLINE", ELEM_ACCESS_ID))
    }

    @Test
    fun numericalLiteralsTest() {
        tokenConverterEquals("1", arrayOf("#1"))
        tokenConverterEquals(".1", arrayOf(ACCESS_ID, "#1"))
        tokenConverterEquals("1.", arrayOf("#1", ACCESS_ID))
        tokenConverterEquals("1.length", arrayOf("#1", ACCESS_ID, "!length"))
        tokenConverterEquals("1.2", arrayOf("#1.2"))
        tokenConverterEquals("1.2.3.4.5", arrayOf("#1.2", ACCESS_ID, "#3.4", ACCESS_ID, "#5"))
    }

    @Test
    fun stringLiteralsTest() {
        tokenConverterEquals("\"abc\"", arrayOf("@abc"))
        tokenConverterThrows(0, "\"abc", StringUnfinishedException::class.java, STRING_UNFINISHED_ERROR)
        tokenConverterEquals("\"\"\"\"", arrayOf("@", "@"))
        tokenConverterThrows(0, "\"\"\"\"\"", StringUnfinishedException::class.java, STRING_UNFINISHED_ERROR)
        tokenConverterThrows(0, "abc\"def\nghi\"jkl", StringUnfinishedException::class.java, null, null)
        tokenConverterThrows(0, "true\n\nhello\"\n\"", StringUnfinishedException::class.java, null,3)
        tokenConverterThrows(0, "@\n\"\n\"\n", StringUnfinishedException::class.java,null, 2)
        tokenConverterThrows(1, "@\n\"\n\"\n", StringUnfinishedException::class.java, null,3)
    }

    @Test
    fun identifiersTest() {
        tokenConverterEquals("a b c", arrayOf("!a", "!b", "!c"))
        tokenConverterEquals("__xXx__", arrayOf("!__xXx__"))
        tokenConverterEquals("a12", arrayOf("!a12"))
        tokenConverterEquals("2ab", arrayOf("#2", "!ab"))
        tokenConverterEquals("français", arrayOf("!français"))
        tokenConverterEquals("日本語", arrayOf("!日本語"))
        tokenConverterEquals("i do not stop the end", arrayOf("!i", DO_ID, "NOT", "BREAK", "!the", "END_BLOCK"))
    }
}