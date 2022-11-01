import `in`.costea.wiles.constants.ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_UNFINISHED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import `in`.costea.wiles.constants.ErrorMessages.TOKEN_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.INVALID_EXPRESSION_ERROR
import `in`.costea.wiles.constants.Tokens.ASSIGN_ID
import `in`.costea.wiles.constants.Tokens.CASE_ID
import `in`.costea.wiles.constants.Tokens.CONTINUE_ID
import `in`.costea.wiles.constants.Tokens.DECLARE_ID
import `in`.costea.wiles.constants.Tokens.DO_ID
import `in`.costea.wiles.constants.Tokens.ELSE_ID
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
import `in`.costea.wiles.constants.Tokens.RETURN_ID
import `in`.costea.wiles.constants.Tokens.RIGHT_ARROW_ID
import `in`.costea.wiles.constants.Tokens.PAREN_END_ID
import `in`.costea.wiles.constants.Tokens.PAREN_START_ID
import `in`.costea.wiles.constants.Tokens.SEPARATOR_ID
import `in`.costea.wiles.constants.Tokens.START_BLOCK_ID
import `in`.costea.wiles.constants.Tokens.TERMINATOR_ID
import `in`.costea.wiles.constants.Tokens.TIMES_ID
import `in`.costea.wiles.constants.Tokens.TRUE_ID
import `in`.costea.wiles.constants.Tokens.TYPEDEF_ID
import `in`.costea.wiles.constants.Tokens.WHEN_ID
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
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)))",
                NEWLINE_ID, NEWLINE_ID,
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)))",
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID,
                NEWLINE_ID, PAREN_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun expressionsTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(!c))))))",
                DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
                PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
                "!b", ASSIGN_ID, "!c", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(#3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
            PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
            "!b", ASSIGN_ID, "#3", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b; PLUS; #3); MINUS; #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; !b); EXPRESSION(UNARY_PLUS; !c); EXPRESSION(EXPRESSION(!a; PLUS; !b); PLUS; !c))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b; PLUS; #3); MINUS; #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!c); ASSIGN; EXPRESSION(EXPRESSION(UNARY_MINUS; #10); PLUS; EXPRESSION(UNARY_PLUS; EXPRESSION(UNARY_PLUS; #10)))))",
                "!c", ASSIGN_ID, MINUS_ID, "#10", PLUS_ID, NEWLINE_ID, PLUS_ID, PAREN_START_ID, PLUS_ID, "#10", PAREN_END_ID)
    }

    @Test
    fun expressionsTestException() {
        assertResults(createExceptions(UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, nullLocation)),
                null,
                "!a", PLUS_ID, "!b", PLUS_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, nullLocation)),
                null,
                "!b", PLUS_ID, TIMES_ID, "#5")
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, nullLocation)),
                null,
                TIMES_ID, "!a")
        assertResults(createExceptions(TokenExpectedException(EXPRESSION_EXPECTED_ERROR, nullLocation)),
                null,
                "!a", PLUS_ID, PAREN_START_ID, "BREAK", PAREN_END_ID)

        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, nullLocation)),
            null,
            "!a", PLUS_ID, PAREN_START_ID,"!b",PLUS_ID, END_BLOCK_ID, PAREN_END_ID)
    }

    @Test
    fun bracketsTests() {
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(EXPRESSION(!b; PLUS; !c); PLUS; !d)))",
                "!a", PLUS_ID, PAREN_START_ID, PAREN_START_ID, "!b", PLUS_ID, "!c",
                PAREN_END_ID, PLUS_ID, "!d", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(!b; PLUS; !c)))",
                "!a", PLUS_ID, PAREN_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(!b; PLUS; !c)))",
                "!a", PLUS_ID, NEWLINE_ID, PAREN_START_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a; PLUS; !b))",
            PAREN_START_ID, "!a", PLUS_ID, "!b", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!a; PLUS; !b); PLUS; !c))",
                PAREN_START_ID, "!a", PLUS_ID, "!b", PAREN_END_ID, PLUS_ID, "!c")
    }

    @Test
    fun programExceptionsTest() {
        //TODO: more, updated tests
        assertResults(createExceptions(UnexpectedEndException(END_OF_STATEMENT_EXPECTED_ERROR, nullLocation)),
            null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID)
        assertResults(createExceptions(TokenExpectedException(END_OF_STATEMENT_EXPECTED_ERROR, nullLocation)),
            null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", PLUS_ID, "!b", DECLARE_ID, NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format("end"), nullLocation)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(")"), nullLocation)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, nullLocation),TokenExpectedException(
            END_OF_STATEMENT_EXPECTED_ERROR, nullLocation)),"CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION; EXPRESSION(!a; PLUS; !b)))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", TIMES_ID, TIMES_ID, "!b", NEWLINE_ID, "!a", PLUS_ID, "!b",
            DECLARE_ID, "!c", ASSIGN_ID, "!d", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, nullLocation)),
        "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK)); EXPRESSION(!a; PLUS; !b))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, CONTINUE_ID
            , NEWLINE_ID, "!a", PLUS_ID, "!b")
    }

    @Test
    fun methodTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, RIGHT_ARROW_ID, "!int",
                START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE INT32); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPEDEF_ID, "!int", PAREN_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE INT32); PARAMETER(!b; TYPE STRING); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPEDEF_ID, "!int",
                SEPARATOR_ID, "!b", TYPEDEF_ID, "!text", PAREN_END_ID, RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE INT32; PARAMETER(!a; TYPE NOTHING); CODE_BLOCK)))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPEDEF_ID, NOTHING_ID, PAREN_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(NOTHING)))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(EXPRESSION(!b); ASSIGN; EXPRESSION(#3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID,
             "!b", ASSIGN_ID, "#3")
        assertResults(null, "CODE_BLOCK(DECLARATION(!product; METHOD(TYPE INT64; PARAMETER(!a; TYPE INT32); PARAMETER(!b; TYPE INT32); CODE_BLOCK(EXPRESSION(EXPRESSION(!product); ASSIGN; EXPRESSION(!a; TIMES; !b))))))",
            DECLARE_ID, "!product", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPEDEF_ID, "!int",
                SEPARATOR_ID, "!b", TYPEDEF_ID, "!int", PAREN_END_ID, RIGHT_ARROW_ID, "!bigint", NEWLINE_ID,
                DO_ID,  "!product", ASSIGN_ID, "!a", TIMES_ID, "!b")
        assertResults(null,"CODE_BLOCK(DECLARATION(!main; METHOD(TYPE NOTHING; PARAMETER(!args; TYPE INT32); CODE_BLOCK(EXPRESSION(NOTHING)))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!args", TYPEDEF_ID, "!int", PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK(DECLARATION(!b; METHOD(TYPE NOTHING; CODE_BLOCK(EXPRESSION(NOTHING))))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, DECLARE_ID,
            "!b", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID, NEWLINE_ID, NEWLINE_ID)
    }

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

    @Test
    fun returnTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a; METHOD(TYPE INT32; CODE_BLOCK(RETURN(EXPRESSION(#10))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, RIGHT_ARROW_ID, "!int",
            START_BLOCK_ID, NEWLINE_ID, RETURN_ID, "#10", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, nullLocation)),null,
            RETURN_ID, "#10")
    }

    @Test
    fun declarationsTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPE INT32; !a; EXPRESSION(#10)))",
            DECLARE_ID, "!a", TYPEDEF_ID, "!int", ASSIGN_ID, "#10")
    }

    @Test
    fun whenTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a; METHOD(TYPE NOTHING; CODE_BLOCK(WHEN(EXPRESSION(TRUE); CODE_BLOCK(RETURN(EXPRESSION(!a))); ELSE; CODE_BLOCK(RETURN(EXPRESSION(!b))))))); WHEN(EXPRESSION(TRUE); CODE_BLOCK(EXPRESSION(!c))); EXPRESSION(!d))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NEWLINE_ID,
            WHEN_ID, TRUE_ID, DO_ID, RETURN_ID, "!a", TERMINATOR_ID, ELSE_ID, DO_ID, RETURN_ID, "!b",
            NEWLINE_ID, WHEN_ID, TRUE_ID, DO_ID, "!c", NEWLINE_ID, "!d")

        assertResults(null,"CODE_BLOCK(WHEN(EXPRESSION(!a; LARGER; #10); CODE_BLOCK(EXPRESSION(NOTHING)); EXPRESSION(!a; LARGER; #0); CODE_BLOCK(EXPRESSION(NOTHING)); ELSE; CODE_BLOCK(EXPRESSION(NOTHING))))",
            WHEN_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID, CASE_ID, "!a", LARGER_ID, "#0",
            DO_ID, NOTHING_ID, NEWLINE_ID, ELSE_ID, DO_ID, NOTHING_ID)

        assertResults(null,"CODE_BLOCK(WHEN(EXPRESSION(!a; LARGER; #0); CODE_BLOCK(EXPRESSION(NOTHING)); ELSE; CODE_BLOCK(WHEN(EXPRESSION(!a; LARGER; #10); CODE_BLOCK(EXPRESSION(NOTHING))))); EXPRESSION(EXPRESSION(!a); ASSIGN; EXPRESSION(!b)))",
            WHEN_ID, "!a", LARGER_ID, "#0", DO_ID, NOTHING_ID, NEWLINE_ID,
            ELSE_ID, DO_ID, WHEN_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID,
            "!a", ASSIGN_ID, "!b" )
    }

    @Test
    fun whileTest()
    {
        //TODO: tests
    }

    @Test
    fun breakContinueTest()
    {
        //TODO: tests
    }

    @Test
    fun methodCallTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION(!b; APPLY; METHOD_CALL)))",
            "!a", PLUS_ID, "!b", PAREN_START_ID, PAREN_END_ID)
        //TODO: tests
    }

    private class CreateConverter(tokens: List<String>) {
        var converter: TokensToSyntaxTreeConverter
        var exceptions: CompilationExceptionsCollection
        var tree: AbstractStatement

        init {
            converter = TokensToSyntaxTreeConverter(tokens.map { content-> Token(content, nullLocation) }, nullLocation)
            tree = converter.convert()
            exceptions = converter.exceptions
        }


        val result: String
            get() = tree.toString()
    }
}