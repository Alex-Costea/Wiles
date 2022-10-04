import `in`.costea.wiles.converters.InputToTokensConverter
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.StringUnfinishedException
import `in`.costea.wiles.exceptions.UnknownOperatorException
import `in`.costea.wiles.statics.Constants.DEBUG
import `in`.costea.wiles.statics.Constants.DO_ID
import `in`.costea.wiles.statics.Constants.MAX_OPERATOR_LENGTH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import java.util.*

class TokenConverterTests {

    private fun tokenConverterEquals(input: String, solution: Array<String>) {
        val solutionList: MutableList<Token> = ArrayList()
        for (s in solution) {
            solutionList.add(Token(s))
        }
        Assertions.assertEquals(InputToTokensConverter(input).convert(), solutionList)
    }

    private fun tokenConverterThrows(exceptionIndex: Int, input: String, throwing: Class<out Throwable>, message: String? = null, line: Int? = null) {
        val x = InputToTokensConverter(input)
        x.convert()
        val t = if (message != null) Assertions.assertThrows(throwing, { x.throwExceptionIfExists(exceptionIndex) }, message) else Assertions.assertThrows(throwing) { x.throwExceptionIfExists(exceptionIndex) }
        assert(t is AbstractCompilationException)
        if (line != null) Assertions.assertEquals(line, Objects.requireNonNull<TokenLocation>((t as AbstractCompilationException).getTokenLocation()).line())
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
        tokenConverterEquals("abc#a b c d e f break end continue", arrayOf("!abc"))
    }

    @Test
    fun operatorsTest() {
        tokenConverterEquals("=/=", arrayOf("NOT_EQUAL"))
        tokenConverterThrows(0, "$", UnknownOperatorException::class.java, null, null)
        tokenConverterThrows(0, "=$", UnknownOperatorException::class.java, "Operator unknown: $")
        val invalidProgram = "\${}{}{}{}{}"
        Assumptions.assumingThat(invalidProgram.length >= MAX_OPERATOR_LENGTH + 1) {
            val substring1 = invalidProgram.substring(1, MAX_OPERATOR_LENGTH + 1)
            tokenConverterThrows(0, invalidProgram, UnknownOperatorException::class.java, "Operator unknown: $substring1")
            Assumptions.assumingThat(invalidProgram.length >= 2 * MAX_OPERATOR_LENGTH + 1) {
                val substring2 = invalidProgram.substring(MAX_OPERATOR_LENGTH + 1, 2 * MAX_OPERATOR_LENGTH + 1)
                tokenConverterThrows(1, invalidProgram, UnknownOperatorException::class.java, "Operator unknown: $substring2")
            }
        }
        if (DEBUG) {
            tokenConverterEquals("$=", arrayOf("TEMP"))
            tokenConverterEquals("=$=", arrayOf("TEMP2"))
        }
        tokenConverterThrows(0, "$\n@", UnknownOperatorException::class.java, "Operator unknown: $")
        tokenConverterThrows(1, "$\n@", UnknownOperatorException::class.java, "Operator unknown: @")
    }

    @Test
    fun numericalLiteralsTest() {
        tokenConverterEquals("1", arrayOf("#1"))
        tokenConverterEquals(".1", arrayOf("DOT", "#1"))
        tokenConverterEquals("1.", arrayOf("#1", "DOT"))
        tokenConverterEquals("1.length", arrayOf("#1", "DOT", "!length"))
        tokenConverterEquals("1.2", arrayOf("#1.2"))
        tokenConverterEquals("1.2.3.4.5", arrayOf("#1.2", "DOT", "#3.4", "DOT", "#5"))
    }

    @Test
    fun stringLiteralsTest() {
        tokenConverterEquals("\"abc\"", arrayOf("@abc"))
        tokenConverterThrows(0, "\"abc", StringUnfinishedException::class.java, "String unfinished: abc")
        tokenConverterEquals("\"\"\"\"", arrayOf("@", "@"))
        tokenConverterThrows(0, "\"\"\"\"\"", StringUnfinishedException::class.java, "String unfinished: ")
        tokenConverterThrows(0, "abc\"def\nghi\"jkl", StringUnfinishedException::class.java, null, null)
        tokenConverterThrows(0, "true\n\nhello\"\n\"", StringUnfinishedException::class.java, null,3)
        tokenConverterThrows(1, "@\n\"\n\"\n", StringUnfinishedException::class.java,null, 2)
        tokenConverterThrows(2, "@\n\"\n\"\n", StringUnfinishedException::class.java, null,3)
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