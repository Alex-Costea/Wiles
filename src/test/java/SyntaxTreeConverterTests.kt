import `in`.costea.wiles.constants.ErrorMessages.END_TOKEN_NOT_ALLOWED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_UNFINISHED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.IDENTIFIER_OR_UNARY_OPERATOR_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.TOKEN_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.UNEXPECTED_TOKEN_ERROR
import `in`.costea.wiles.constants.Tokens.ASSIGN_ID
import `in`.costea.wiles.constants.Tokens.DECLARE_ID
import `in`.costea.wiles.constants.Tokens.DO_ID
import `in`.costea.wiles.constants.Tokens.END_BLOCK_ID
import `in`.costea.wiles.constants.Tokens.EQUALS_ID
import `in`.costea.wiles.constants.Tokens.LARGER_ID
import `in`.costea.wiles.constants.Tokens.METHOD_ID
import `in`.costea.wiles.constants.Tokens.MINUS_ID
import `in`.costea.wiles.constants.Tokens.NEWLINE_ID
import `in`.costea.wiles.constants.Tokens.NOTHING_ID
import `in`.costea.wiles.constants.Tokens.NOT_ID
import `in`.costea.wiles.constants.Tokens.OR_ID
import `in`.costea.wiles.constants.Tokens.PLUS_ID
import `in`.costea.wiles.constants.Tokens.POWER_ID
import `in`.costea.wiles.constants.Tokens.RIGHT_ARROW_ID
import `in`.costea.wiles.constants.Tokens.ROUND_BRACKET_END_ID
import `in`.costea.wiles.constants.Tokens.ROUND_BRACKET_START_ID
import `in`.costea.wiles.constants.Tokens.SEPARATOR_ID
import `in`.costea.wiles.constants.Tokens.START_BLOCK_ID
import `in`.costea.wiles.constants.Tokens.TIMES_ID
import `in`.costea.wiles.constants.Tokens.TYPEOF_ID
import `in`.costea.wiles.constants.Utils.nullLocation
import `in`.costea.wiles.converters.TokensToSyntaxTreeConverter
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.TokenExpectedException
import `in`.costea.wiles.exceptions.UnexpectedEndException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.statements.AbstractStatement
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SyntaxTreeConverterTests {
    private fun assertResults(exceptions: CompilationExceptionsCollection?, expectedResult: String?, vararg tokens: String) {
        val exceptionList = exceptions ?: CompilationExceptionsCollection()
        val converter = CreateConverter(tokens.asList())
        Assertions.assertEquals(exceptionList, converter.exceptions)
        expectedResult?.let{Assertions.assertEquals(expectedResult, converter.result)}
    }

    private fun createExceptions(vararg list: AbstractCompilationException): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        exceptions.addAll(listOf(*list))
        return exceptions
    }

    @Test
    fun newlineTests() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)))",
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)))",
                NEWLINE_ID, NEWLINE_ID,
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)))",
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID,
                NEWLINE_ID, ROUND_BRACKET_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun expressionsTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(!c))))))",
                DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
                ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID,
                "!b", ASSIGN_ID, "!c", END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(#3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
            ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID,
            "!b", ASSIGN_ID, "#3", END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b; PLUS; #3); MINUS; #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; !b); EXPRESSION(UNARY_PLUS; !c); EXPRESSION(EXPRESSION(!a; PLUS; !b); PLUS; !c))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b; PLUS; #3); MINUS; #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!c); ASSIGN; EXPRESSION(EXPRESSION(UNARY_MINUS; #10); PLUS; EXPRESSION(UNARY_PLUS; EXPRESSION(UNARY_PLUS; #10)))))",
                "!c", ASSIGN_ID, MINUS_ID, "#10", PLUS_ID, NEWLINE_ID, PLUS_ID, ROUND_BRACKET_START_ID, PLUS_ID, "#10", ROUND_BRACKET_END_ID)
    }

    @Test
    fun expressionsTestException() {
        assertResults(createExceptions(UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, nullLocation)),
                null,
                "!a", PLUS_ID, "!b", PLUS_ID)
        assertResults(createExceptions(TokenExpectedException(IDENTIFIER_OR_UNARY_OPERATOR_EXPECTED_ERROR, nullLocation)),
                null,
                "!b", PLUS_ID, TIMES_ID, "#5")
        assertResults(createExceptions(UnexpectedTokenException(UNEXPECTED_TOKEN_ERROR, nullLocation)),
                null,
                TIMES_ID, "!a")
        assertResults(createExceptions(TokenExpectedException(EXPRESSION_EXPECTED_ERROR, nullLocation)),
                null,
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, "BREAK", ROUND_BRACKET_END_ID)

        assertResults(createExceptions(UnexpectedTokenException(END_TOKEN_NOT_ALLOWED_ERROR, nullLocation)),
            null,
            "!a", PLUS_ID, ROUND_BRACKET_START_ID,"!b",PLUS_ID, END_BLOCK_ID, ROUND_BRACKET_END_ID)
    }

    @Test
    fun bracketsTests() {
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(EXPRESSION(!b; PLUS; !c); PLUS; !d)))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c",
                ROUND_BRACKET_END_ID, PLUS_ID, "!d", ROUND_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(!b; PLUS; !c)))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(!b; PLUS; !c)))",
                "!a", PLUS_ID, NEWLINE_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; !b))",
            ROUND_BRACKET_START_ID, "!a", PLUS_ID, "!b", ROUND_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!a; PLUS; !b); PLUS; !c))",
                ROUND_BRACKET_START_ID, "!a", PLUS_ID, "!b", ROUND_BRACKET_END_ID, PLUS_ID, "!c")
    }

    @Test
    fun programExceptionsTest() {
        //TODO: more, updated tests
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format("end"), nullLocation)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(")"), nullLocation)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID)
    }

    @Test
    fun methodTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, RIGHT_ARROW_ID, "!int",
                START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE INT32); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, "!a", TYPEOF_ID, "!int", ROUND_BRACKET_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE INT32); PARAMETER(!b; TYPE STRING); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, "!a", TYPEOF_ID, "!int",
                SEPARATOR_ID, "!b", TYPEOF_ID, "!text", ROUND_BRACKET_END_ID, RIGHT_ARROW_ID, "!int", START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE NOTHING); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, "!a", TYPEOF_ID, NOTHING_ID, ROUND_BRACKET_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(NOTHING)))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, DO_ID, NOTHING_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(#3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, DO_ID,
             "!b", ASSIGN_ID, "#3")
        assertResults(null, "CODE_BLOCK(DECLARATION(!product; METHOD(TYPE INT64; PARAMETER(!a; TYPE INT32); PARAMETER(!b; TYPE INT32); CODE_BLOCK(EXPRESSION(EXPRESSION(!product); ASSIGN; EXPRESSION(!a; TIMES; !b))))))",
            DECLARE_ID, "!product", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, "!a", TYPEOF_ID, "!int",
                SEPARATOR_ID, "!b", TYPEOF_ID, "!int", ROUND_BRACKET_END_ID, RIGHT_ARROW_ID, "!bigint", NEWLINE_ID,
                DO_ID,  "!product", ASSIGN_ID, "!a", TIMES_ID, "!b")
        assertResults(null,"CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; PARAMETER ANON(!arg1; TYPE INT32); CODE_BLOCK(EXPRESSION(NOTHING)))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, "!arg1", TYPEOF_ID, "!int", ROUND_BRACKET_END_ID, DO_ID, NOTHING_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK(DECLARATION(!b; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(NOTHING))))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, DECLARE_ID,
            "!b", ASSIGN_ID, METHOD_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID, NEWLINE_ID, NEWLINE_ID)
    }

    //TODO: figure out a better internal representation for square bracket calling
    // It should be some kind of method call probably
/*    @Test
    fun squareBracketsTest() {
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; EXPRESSION SQUARE(!b; COMMA; !d; COMMA; !e; PLUS; #2; TIMES; EXPRESSION(!a; POWER; !b))))",
                "!a", SQUARE_BRACKET_START_ID, "!b", COMMA_ID, "!d", COMMA_ID, "!e",
                PLUS_ID, "#2", TIMES_ID, ROUND_BRACKET_START_ID, "!a", POWER_ID, "!b", ROUND_BRACKET_END_ID, SQUARE_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; EXPRESSION SQUARE(!b)))",
                "!a", SQUARE_BRACKET_START_ID, "!b", COMMA_ID, SQUARE_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(ASSIGNMENT(EXPRESSION(!a); EXPRESSION(!b; EXPRESSION SQUARE(#3; COMMA; #4; COMMA; #5))))",
                "!a", ASSIGN_ID,"!b", SQUARE_BRACKET_START_ID, "#3", COMMA_ID, "#4", COMMA_ID, "#5", COMMA_ID, SQUARE_BRACKET_END_ID)
        assertResults(null, "CODE_BLOCK(ASSIGNMENT(EXPRESSION(!a); EXPRESSION(!b; EXPRESSION SQUARE(!c; COMMA; !d; EXPRESSION SQUARE(!e)); PLUS; !f)))",
                "!a", ASSIGN_ID, "!b", SQUARE_BRACKET_START_ID, "!c", COMMA_ID, "!d",
                SQUARE_BRACKET_START_ID, "!e", SQUARE_BRACKET_END_ID, SQUARE_BRACKET_END_ID, PLUS_ID, "!f")
    }*/

    @Test
    fun orderOfOperationsTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(!a; OR; EXPRESSION(EXPRESSION(!b; PLUS; !c); LARGER; !d)))",
            "!a", OR_ID, "!b", PLUS_ID, "!c", LARGER_ID, "!d")

        assertResults(null,"CODE_BLOCK(EXPRESSION(NOT; EXPRESSION(EXPRESSION(!a; PLUS; !b); EQUALS; #10)))",
        NOT_ID,"!a", PLUS_ID,"!b", EQUALS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(!a; TIMES; EXPRESSION(!b; POWER; #2)); PLUS; #10))",
            "!a", TIMES_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(!a; POWER; EXPRESSION(!b; POWER; #2)); PLUS; #10))",
            "!a", POWER_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(NOT; EXPRESSION(NOT; EXPRESSION(!a; EQUALS; !b))); OR; !c))",
            NOT_ID, NOT_ID, "!a", EQUALS_ID, "!b", OR_ID, "!c")
    }

    private class CreateConverter(tokens: List<String>) {
        var converter: TokensToSyntaxTreeConverter
        var exceptions: CompilationExceptionsCollection
        var tree: AbstractStatement

        init {
            converter = TokensToSyntaxTreeConverter(tokens.map { content-> Token(content, nullLocation) })
            tree = converter.convert()
            exceptions = converter.exceptions
        }


        val result: String
            get() = tree.toString()
    }
}