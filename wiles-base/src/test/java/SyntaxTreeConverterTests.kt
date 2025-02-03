
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.parser.converters.TokensToSyntaxTreeConverter
import wiles.parser.exceptions.TokenExpectedException
import wiles.parser.exceptions.UnexpectedEndException
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.CONST_CANT_BE_VAR_ERROR
import wiles.shared.constants.ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR
import wiles.shared.constants.ErrorMessages.EXPECTED_GLOBAL_VALUE_ERROR
import wiles.shared.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import wiles.shared.constants.ErrorMessages.EXPRESSION_UNFINISHED_ERROR
import wiles.shared.constants.ErrorMessages.INVALID_EXPRESSION_ERROR
import wiles.shared.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import wiles.shared.constants.ErrorMessages.TOKEN_EXPECTED_ERROR
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.BREAK_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.CONTINUE_ID
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.FOR_ID
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.GLOBAL_ID
import wiles.shared.constants.Tokens.IF_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.IS_ID
import wiles.shared.constants.Tokens.LARGER_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.NEWLINE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.Tokens.RETURN_ID
import wiles.shared.constants.Tokens.RIGHT_ARROW_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.TERMINATOR_ID
import wiles.shared.constants.Tokens.TIMES_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.Tokens.TYPE_ANNOTATION_ID
import wiles.shared.constants.Tokens.TYPE_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.constants.Tokens.WHEN_ID
import wiles.shared.constants.Tokens.WHILE_ID
import wiles.shared.constants.Utils.NULL_LOCATION

@Testable
class SyntaxTreeConverterTests {
    private fun trimAll(string: String) : String
    {
        return string.replace("[\\r\\n]+| |\\[.*]".toRegex(),"")
    }

    private fun assertResults(exceptions: CompilationExceptionsCollection?, expectedResult: String?, vararg tokens: String) {
        val exceptionList = exceptions ?: CompilationExceptionsCollection()
        val converter = CreateConverter(tokens.asList())
        Assertions.assertEquals(exceptionList, converter.exceptions)
        expectedResult?.let{
            val expected = trimAll(expectedResult)
            val got = trimAll(converter.result)
            Assertions.assertEquals(expected, got)
        }
    }

    private fun createExceptions(vararg list: AbstractCompilationException): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        exceptions.addAll(listOf(*list))
        return exceptions
    }

    @Test
    fun newlineTests() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK))))",
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK))))",
                NEWLINE_ID, NEWLINE_ID,
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK))))",
                DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID,
                NEWLINE_ID, PAREN_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun expressionsTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(!c)))))))",
                DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
                PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
                "!b", ASSIGN_ID, "!c", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(#3)))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID,
            PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
            "!b", ASSIGN_ID, "#3", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b, %PLUS, #3), %MINUS, #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a, %PLUS, !b), EXPRESSION(%UNARY_PLUS, !c), EXPRESSION(EXPRESSION(!a, %PLUS, !b), %PLUS, !c))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!b, %PLUS, #3), %MINUS, #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!c), %ASSIGN, EXPRESSION(EXPRESSION(%UNARY_MINUS, #10), %PLUS, EXPRESSION(%UNARY_PLUS, EXPRESSION(%UNARY_PLUS, #10)))))",
                "!c", ASSIGN_ID, MINUS_ID, "#10", PLUS_ID, NEWLINE_ID, PLUS_ID, PAREN_START_ID, PLUS_ID, "#10", PAREN_END_ID)
    }

    @Test
    fun expressionsTestException() {
        assertResults(createExceptions(UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, NULL_LOCATION)),
                null,
                "!a", PLUS_ID, "!b", PLUS_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, NULL_LOCATION)),
                null,
                "!b", PLUS_ID, TIMES_ID, "#5")
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
                null,
                TIMES_ID, "!a")
        assertResults(createExceptions(TokenExpectedException(EXPRESSION_EXPECTED_ERROR, NULL_LOCATION)),
                null,
                "!a", PLUS_ID, PAREN_START_ID, "BREAK", PAREN_END_ID)

        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, NULL_LOCATION)),
            null,
            "!a", PLUS_ID, PAREN_START_ID,"!b",PLUS_ID, END_BLOCK_ID, PAREN_END_ID)
    }

    @Test
    fun bracketsTests() {
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a, %PLUS, EXPRESSION(EXPRESSION(!b, %PLUS, !c), %PLUS, !d)))",
                "!a", PLUS_ID, PAREN_START_ID, PAREN_START_ID, "!b", PLUS_ID, "!c",
                PAREN_END_ID, PLUS_ID, "!d", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a, %PLUS, EXPRESSION(!b, %PLUS, !c)))",
                "!a", PLUS_ID, PAREN_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a, %PLUS, EXPRESSION(!b, %PLUS, !c)))",
                "!a", PLUS_ID, NEWLINE_ID, PAREN_START_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!a, %PLUS, !b))",
            PAREN_START_ID, "!a", PLUS_ID, "!b", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(EXPRESSION(!a, %PLUS, !b), %PLUS, !c))",
                PAREN_START_ID, "!a", PLUS_ID, "!b", PAREN_END_ID, PLUS_ID, "!c")
    }

    @Test
    fun programExceptionsTest() {
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(END_BLOCK_ID), NULL_LOCATION)),
            null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID)
        assertResults(createExceptions(TokenExpectedException(END_OF_STATEMENT_EXPECTED_ERROR, NULL_LOCATION)),
            null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", PLUS_ID, "!b", DECLARE_ID, NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(END_BLOCK_ID), NULL_LOCATION)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(PAREN_END_ID), NULL_LOCATION)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, NULL_LOCATION),TokenExpectedException(
            END_OF_STATEMENT_EXPECTED_ERROR, NULL_LOCATION)),"CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION, EXPRESSION(!a, %PLUS, !b))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", TIMES_ID, TIMES_ID, "!b", NEWLINE_ID, "!a", PLUS_ID, "!b",
            DECLARE_ID, "!c", ASSIGN_ID, "!d", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
        "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK))), EXPRESSION(!a, %PLUS, !b))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, CONTINUE_ID
            , NEWLINE_ID, "!a", PLUS_ID, "!b")
    }

    @Test
    fun methodTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(TYPE: INT, CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, RIGHT_ARROW_ID, "!int",
                START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(TYPE: INT, DECLARATION(TYPE: INT, !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPE_ANNOTATION_ID, "!int", PAREN_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(TYPE: INT, DECLARATION(TYPE: INT, !a), DECLARATION(TYPE: STRING, !b), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPE_ANNOTATION_ID, "!int",
                SEPARATOR_ID, "!b", TYPE_ANNOTATION_ID, "!text", PAREN_END_ID, RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(TYPE: INT, DECLARATION(TYPE: !nothing, !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPE_ANNOTATION_ID, NOTHING_ID, PAREN_END_ID,
            RIGHT_ARROW_ID, "!int", START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION(!nothing))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(#3)))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID,
             "!b", ASSIGN_ID, "#3")
        assertResults(null, "CODE_BLOCK(DECLARATION(!product, EXPRESSION(METHOD(TYPE: INT, DECLARATION(TYPE: INT, !a), DECLARATION(TYPE: INT, !b), CODE_BLOCK(EXPRESSION(EXPRESSION(!product), %ASSIGN, EXPRESSION(!a, %TIMES, !b)))))))",
            DECLARE_ID, "!product", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!a", TYPE_ANNOTATION_ID, "!int",
                SEPARATOR_ID, "!b", TYPE_ANNOTATION_ID, "!int", PAREN_END_ID, RIGHT_ARROW_ID, "!int", NEWLINE_ID,
                DO_ID,  "!product", ASSIGN_ID, "!a", TIMES_ID, "!b")
        assertResults(null,"CODE_BLOCK(DECLARATION(!main, EXPRESSION(METHOD(DECLARATION(TYPE: INT, !args), CODE_BLOCK(EXPRESSION(!nothing))))))",
            DECLARE_ID, "!main", ASSIGN_ID, METHOD_ID, PAREN_START_ID, "!args", TYPE_ANNOTATION_ID, "!int", PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK(DECLARATION(!b, EXPRESSION(METHOD(CODE_BLOCK(EXPRESSION(!nothing))))))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, DECLARE_ID,
            "!b", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID, NEWLINE_ID, NEWLINE_ID)
    }

    @Test
    fun orderOfOperationsTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(!a, %OR, EXPRESSION(EXPRESSION(!b, %PLUS, !c), %LARGER, !d)))",
            "!a", OR_ID, "!b", PLUS_ID, "!c", LARGER_ID, "!d")

        assertResults(null,"CODE_BLOCK(EXPRESSION(%NOT, EXPRESSION(EXPRESSION(!a, %PLUS, !b), %EQUALS, #10)))",
        NOT_ID,"!a", PLUS_ID,"!b", EQUALS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(!a, %TIMES, EXPRESSION(!b, %POWER, #2)), %PLUS, #10))",
            "!a", TIMES_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(!a, %POWER, EXPRESSION(!b, %POWER, #2)), %PLUS, #10))",
            "!a", POWER_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(EXPRESSION(%NOT, EXPRESSION(%NOT, EXPRESSION(!a, %EQUALS, !b))), %OR, !c))",
            NOT_ID, NOT_ID, "!a", EQUALS_ID, "!b", OR_ID, "!c")
    }

    @Test
    fun returnTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(TYPE: INT, CODE_BLOCK(RETURN(EXPRESSION(#10)))))))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, RIGHT_ARROW_ID, "!int",
            START_BLOCK_ID, NEWLINE_ID, RETURN_ID, "#10", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),null,
            RETURN_ID, "#10")
    }

    @Test
    fun declarationsTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPE: INT, !a, EXPRESSION(#10)))",
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!int", ASSIGN_ID, "#10")
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(ASSIGN_ID), NULL_LOCATION)),
            "CODE_BLOCK(DECLARATION(TYPE: INT, !a, EXPRESSION(#2)), DECLARATION(!a, EXPRESSION(#2)), DECLARATION(TYPE: INT, !a), DECLARATION(!a))",
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!int", ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!int", NEWLINE_ID, DECLARE_ID, "!a")
    }

    @Test
    fun ifTest()
    {
        assertResults(null, "CODE_BLOCK(IF(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(!nothing))), EXPRESSION(!nothing))",
            IF_ID, TRUE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, NOTHING_ID)

        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(METHOD(CODE_BLOCK(IF(EXPRESSION(!true), CODE_BLOCK(RETURN(EXPRESSION(#1))), %ELSE, CODE_BLOCK(RETURN(EXPRESSION(#2)))))))), IF(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(!nothing))), EXPRESSION(!nothing))",
            DECLARE_ID, "!a", ASSIGN_ID, METHOD_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NEWLINE_ID,
            IF_ID, START_BLOCK_ID, TRUE_ID, DO_ID, RETURN_ID, "#1", NEWLINE_ID,
            ELSE_ID, DO_ID, RETURN_ID, "#2", NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID,
            IF_ID, TRUE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, NOTHING_ID)

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(!a, %LARGER, #10), CODE_BLOCK(EXPRESSION(!nothing)), EXPRESSION(!a, %LARGER, #0), CODE_BLOCK(EXPRESSION(!nothing)), %ELSE, CODE_BLOCK(EXPRESSION(!nothing))))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID, "!a", LARGER_ID, "#0",
            DO_ID, NOTHING_ID, NEWLINE_ID, ELSE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID)

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(!a, %LARGER, #0), CODE_BLOCK(EXPRESSION(!nothing)), %ELSE, CODE_BLOCK(IF(EXPRESSION(!a, %LARGER, #10), CODE_BLOCK(EXPRESSION(!nothing))))), EXPRESSION(EXPRESSION(!a), %ASSIGN, EXPRESSION(!b)))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#0", DO_ID, NOTHING_ID, NEWLINE_ID,
            ELSE_ID, DO_ID, IF_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID,NEWLINE_ID, END_BLOCK_ID,
            NEWLINE_ID, "!a", ASSIGN_ID, "!b")

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(!a, %LARGER, #10), CODE_BLOCK(EXPRESSION(!nothing)), EXPRESSION(!a, %LARGER, #0), CODE_BLOCK(EXPRESSION(!nothing))))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID,
            "!a", LARGER_ID, "#0", DO_ID, NOTHING_ID, NEWLINE_ID,NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun forTest()
    {
        assertResults(null,"CODE_BLOCK(FOR(!i, %IN, EXPRESSION(!list), %FROM, EXPRESSION(#1), %TO, EXPRESSION(#100), CODE_BLOCK(EXPRESSION(!write_line, %APPLY, METHOD_CALL(EXPRESSION('hello!'))))))",
            FOR_ID, "!i", IN_ID, "!list", FROM_ID, "#1", TO_ID, "#100", NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, "!write_line", PAREN_START_ID, "@hello!", PAREN_END_ID, NEWLINE_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(FOR(!i, CODE_BLOCK(EXPRESSION(!nothing))))",
            FOR_ID, "!i", DO_ID, NOTHING_ID)
    }

    @Test
    fun whileTest()
    {
        assertResults(null,"CODE_BLOCK(WHILE(EXPRESSION(!true), CODE_BLOCK(CONTINUE)))",
            WHILE_ID, TRUE_ID, START_BLOCK_ID, NEWLINE_ID, CONTINUE_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun breakContinueErrorTest()
    {
        assertResults(null,"CODE_BLOCK(WHILE(EXPRESSION(!true), CODE_BLOCK(BREAK)))",
            WHILE_ID, TRUE_ID, DO_ID, BREAK_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK", BREAK_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK", CONTINUE_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION),
                                       TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK(WHILE(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(METHOD(CODE_BLOCK)), EXPRESSION(METHOD(CODE_BLOCK)))))",
            WHILE_ID, TRUE_ID, START_BLOCK_ID, NEWLINE_ID, DO_ID, BREAK_ID,
            NEWLINE_ID, DO_ID, CONTINUE_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun methodCallTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(!a, %PLUS, EXPRESSION(!b, %APPLY, METHOD_CALL)))",
            "!a", PLUS_ID, "!b", PAREN_START_ID, PAREN_END_ID)
        assertResults(null,"CODE_BLOCK(EXPRESSION(!min, %APPLY, METHOD_CALL(EXPRESSION(EXPRESSION(!list), %ASSIGN, EXPRESSION(!a)))))",
            "!min", PAREN_START_ID, "!list", ASSIGN_ID, "!a", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!call, %APPLY, METHOD_CALL(EXPRESSION(EXPRESSION(!a), %ASSIGN, EXPRESSION(!b, %PLUS, !c)), EXPRESSION(!d), EXPRESSION(!e, %EQUALS, !f))))",
            "!call", PAREN_START_ID, "!a", ASSIGN_ID, "!b", PLUS_ID, "!c", SEPARATOR_ID, "!d", SEPARATOR_ID, "!e", EQUALS_ID, "!f", PAREN_END_ID)
    }

    @Test
    fun typesTest()
    {
        assertResults(null, "CODE_BLOCK(DECLARATION(TYPE: METHOD (METHOD), !a))",
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, METHOD_ID, BRACKET_START_ID, BRACKET_END_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPE: METHOD (METHOD(TYPE: BOOLEAN, DECLARATION(TYPE: INT, !a), DECLARATION(TYPE: STRING, !b))), !func))",
            DECLARE_ID, "!func", TYPE_ANNOTATION_ID, METHOD_ID, BRACKET_START_ID, "!a", TYPE_ANNOTATION_ID, "!int", SEPARATOR_ID,
            "!b", TYPE_ANNOTATION_ID, "!text", SEPARATOR_ID, RIGHT_ARROW_ID, "!truth", BRACKET_END_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPE: EITHER (TYPE: INT, TYPE: !nothing), !a), DECLARATION(TYPE: EITHER (TYPE: INT, TYPE: !nothing), !b))",
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!int", MAYBE_ID, NEWLINE_ID,
            DECLARE_ID, "!b", TYPE_ANNOTATION_ID, "!int", OR_ID, NOTHING_ID)
    }

    @Test
    fun whenTests()
    {
        assertResults(null,"CODE_BLOCK(WHEN(EXPRESSION(!a), TYPE: INT, CODE_BLOCK(EXPRESSION(!nothing)), TYPE: STRING, CODE_BLOCK(EXPRESSION(!nothing)), %ELSE, CODE_BLOCK(EXPRESSION(!nothing))))",
            WHEN_ID, "!a", START_BLOCK_ID, NEWLINE_ID,
            IS_ID, "!int", DO_ID, NOTHING_ID, NEWLINE_ID,
            IS_ID, "!text", DO_ID, NOTHING_ID, NEWLINE_ID,
            ELSE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun listTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(LIST(TYPE: INT, EXPRESSION(#1), EXPRESSION(#2), EXPRESSION(#3)))))",
            DECLARE_ID, "!a", ASSIGN_ID, BRACKET_START_ID,
            "#1", SEPARATOR_ID, "#2", SEPARATOR_ID, "#3", SEPARATOR_ID,
            BRACKET_END_ID, TYPE_ANNOTATION_ID, "!int")
    }

    @Test
    fun typeLiteralsTest()
    {
        /*
              let my_TYPE: := type(list[int or text]?)
              let a : 1 + 2 * 3 := 123
         */
        assertResults(null,"CODE_BLOCK(DECLARATION(!my_type, EXPRESSION(TYPE_LITERAL: EITHER (TYPE: LIST (TYPE: EITHER (TYPE: INT, TYPE: STRING)), TYPE: !nothing))), DECLARATION(TYPE: EXPRESSION (#1, %PLUS, EXPRESSION(#2, %TIMES, #3)), !a, EXPRESSION(#123)))",
            DECLARE_ID, "!my_type", ASSIGN_ID, TYPE_ID,
            PAREN_START_ID, "!list", BRACKET_START_ID, "!int", OR_ID, "!text", BRACKET_END_ID, MAYBE_ID, PAREN_END_ID,
            NEWLINE_ID, DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "#1", PLUS_ID, "#2", TIMES_ID, "#3", ASSIGN_ID, "#123")

        /*
        let a : 17? := 17
        let a : true or false := 17
        let a : either[1,2] := 17
         */
        assertResults(null, "CODE_BLOCK(DECLARATION(TYPE: EITHER (TYPE: EXPRESSION (#17), TYPE: !nothing), !a, EXPRESSION(#17)), DECLARATION(TYPE: EXPRESSION (!true, %OR, !false), !a, EXPRESSION(#17)), DECLARATION(TYPE: EITHER (TYPE: EXPRESSION (#1), TYPE: EXPRESSION (#2)), !a, EXPRESSION(#17)))",
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "#17", MAYBE_ID, ASSIGN_ID, "#17", NEWLINE_ID,
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!true", OR_ID, "!false", ASSIGN_ID, "#17", NEWLINE_ID,
            DECLARE_ID, "!a", TYPE_ANNOTATION_ID, "!either", BRACKET_START_ID, "#1", SEPARATOR_ID, "#2",
            BRACKET_END_ID, ASSIGN_ID, "#17")
    }

    @Test
    fun constTest(){
        //let const a : const[int] := 123
        assertResults(null,"""
        CODE_BLOCK(
            DECLARATION: CONST 
            (
                TYPE: CONST [1, 15, 1, 20] 
                (
                    TYPE: INT [1, 21, 1, 24] 
                ), 
                !a [1, 11, 1, 12], 
                EXPRESSION
                (
                    #123 [1, 29, 1, 32]
                )
            )
        )
""", DECLARE_ID, CONST_ID, "!a", TYPE_ANNOTATION_ID, CONST_ID, BRACKET_START_ID, "!int", BRACKET_END_ID, ASSIGN_ID, "#123")

        //let const var a : const[int] := 123
        assertResults(createExceptions(UnexpectedTokenException(CONST_CANT_BE_VAR_ERROR, NULL_LOCATION)),
            """
                CODE_BLOCK
                (
                    DECLARATION
                )
        """, DECLARE_ID, CONST_ID, VARIABLE_ID, "!a",
            TYPE_ANNOTATION_ID, CONST_ID, BRACKET_START_ID, "!int", BRACKET_END_ID, ASSIGN_ID, "#123")
    }

    @Test
    fun globalDeclarationTest()
    {
        // let def a := 234
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION: GLOBAL
                (
                    !a [1, 12, 1, 13], 
                    EXPRESSION
                    (
                        #234 [1, 17, 1, 20]
                    )
                )
            )
        """, DECLARE_ID, GLOBAL_ID, "!a", ASSIGN_ID, "#234")

        //let def a : int
        assertResults(createExceptions(TokenExpectedException(EXPECTED_GLOBAL_VALUE_ERROR, NULL_LOCATION)),
            """
                CODE_BLOCK
                (
                    DECLARATION: GLOBAL
                    (
                        TYPE: INT [1, 16, 1, 19] , 
                        !a [1, 12, 1, 13]
                    )
                )
            """, DECLARE_ID, GLOBAL_ID, "!a", TYPE_ANNOTATION_ID, "!int")
    }

    @Test
    fun textTest()
    {
        // write_line("It's a beautiful day! but i still feel sad :-\b; sorry")
        assertResults(null,"CODE_BLOCK(EXPRESSION(!write_line, %APPLY, METHOD_CALL(EXPRESSION('It\\qs\\sa\\sbeautiful\\sday!\\sbut\\si\\sstill\\sfeel\\ssad\\s:-\\b\\ssorry\\s-_-\\noh\\swell'))))",
            "!write_line", PAREN_START_ID, "@It's a beautiful day! but i still feel sad :-\\ sorry -_-\noh well", PAREN_END_ID, TERMINATOR_ID)
    }

    private class CreateConverter(tokens: List<String>) {
        var converter  =
            TokensToSyntaxTreeConverter(tokens.map { content-> Token(content, NULL_LOCATION) }, NULL_LOCATION)
        var exceptions = converter.exceptions
        var tree = converter.convert()


        val result: String
            get() = tree.toString()
    }
}