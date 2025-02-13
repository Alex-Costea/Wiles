
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import wiles.parser.converters.InputToTokensConverter
import wiles.parser.exceptions.StringInvalidException
import wiles.shared.AbstractCompilationException
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.STRING_UNFINISHED_ERROR
import wiles.shared.constants.Tokens.ACCESS_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.MAX_SYMBOL_LENGTH
import wiles.shared.constants.Tokens.NEWLINE_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.WHILE_ID
import wiles.shared.constants.Utils.NULL_LOCATION
import java.util.*

class TokenConverterTests {

    private fun tokenConverterEquals(input: String, solution: Array<String>) {
        val solutionList: MutableList<Token> = ArrayList()
        for (s in solution) {
            solutionList.add(Token(s, NULL_LOCATION))
        }
        val givenList = InputToTokensConverter(input, NULL_LOCATION).convert()
        assert(givenList.size == solutionList.size)
        for((i,x) in givenList.withIndex())
            assert(x.content == solutionList[i].content)
    }

    private fun tokenConverterThrows(exceptionIndex: Int, input: String, throwing: Class<out Throwable>, message: String? = null, line: Int? = null) {
        val x = InputToTokensConverter(input, NULL_LOCATION)
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
        tokenConverterEquals("abc#a b c d e f break end continue", arrayOf("!abc"))
    }

    @Suppress("KotlinConstantConditions")
    @Test
    fun symbolsTest() {
        tokenConverterEquals("=/=", arrayOf("NOT_EQUAL"))
        tokenConverterEquals( "$", arrayOf("$"))
        tokenConverterEquals( "=$", arrayOf("EQUALS","$"))
        val invalidProgram = "\$⟨⟩⟨⟩⟨⟩⟨⟩⟨⟩"
        Assumptions.assumingThat(invalidProgram.length >= 2 * MAX_SYMBOL_LENGTH + 1) {
            tokenConverterEquals( invalidProgram, arrayOf("\$⟨⟩","⟨⟩⟨","⟩⟨⟩","⟨⟩"))
        }
        tokenConverterEquals( "$\n+", arrayOf("$", NEWLINE_ID, PLUS_ID))
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
        tokenConverterThrows(0, "\"abc", StringInvalidException::class.java, STRING_UNFINISHED_ERROR)
        tokenConverterEquals("\"\"\"\"", arrayOf("@", "@"))
        tokenConverterThrows(0, "\"\"\"\"\"", StringInvalidException::class.java, STRING_UNFINISHED_ERROR)
        tokenConverterEquals("\"Hello, world.\\n\"", arrayOf("@Hello, world.\n"))
        tokenConverterEquals("\"Hello, world.\\n;\"", arrayOf("@Hello, world.\n"))
        tokenConverterEquals("\"Hello, world\\n\\s\"", arrayOf("@Hello, world\n;"))
        tokenConverterEquals("\"My name is \\qAlex\\q. Ugly right :-\\b\"",
            arrayOf("@My name is \"Alex\". Ugly right :-\\"))
        tokenConverterEquals("\"This is a backslash: \\. This is a dollar sign: \$. cool right?\"",
            arrayOf("@This is a backslash: \\. This is a dollar sign: \$. cool right?"))
        tokenConverterEquals("\"Welcome to your Wiles\\copy; free tri\\#xE1;l!\\NewLine;Purchase a license for only 999\\#8364;!\"",
            arrayOf("@Welcome to your Wiles© free triál!\nPurchase a license for only 999€!"))
        tokenConverterEquals("\"\\#xzzzz; \\u\\u;\"", arrayOf("@\\#xzzzz; \\u\\u;"))
        tokenConverterEquals("\"\\#x1F600;\"", arrayOf("@\uD83D\uDE00"))
    }

    @Test
    fun identifiersTest() {
        tokenConverterEquals("a b c", arrayOf("!a", "!b", "!c"))
        tokenConverterEquals("__xXx__", arrayOf("!__xXx__"))
        tokenConverterEquals("a12", arrayOf("!a12"))
        tokenConverterEquals("2ab", arrayOf("#2", "!ab"))
        tokenConverterEquals("français", arrayOf("!français"))
        tokenConverterEquals("日本語", arrayOf("!日本語"))
        tokenConverterEquals("i do not the end", arrayOf("!i", DO_ID, NOT_ID, "!the", END_BLOCK_ID))
    }

    @Test
    fun whitespaceTest()
    {
        tokenConverterEquals("while\ttrue do\n" +
                "\t\twrite_line(\t\"\thi! " +
                "what's up?\") #this is a comment",
            arrayOf(WHILE_ID, "!true", DO_ID, NEWLINE_ID, "!write_line", "PAREN_START",
                "@\thi! what's up?", PAREN_END_ID))
    }
}