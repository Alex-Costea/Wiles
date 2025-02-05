
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
import wiles.shared.constants.Tokens.ANNOTATE_ID
import wiles.shared.constants.Tokens.ANYTHING_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.BREAK_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.CONTINUE_ID
import wiles.shared.constants.Tokens.DECIMAL_ID
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.FOR_ID
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.FUNC_TYPE_ID
import wiles.shared.constants.Tokens.GLOBAL_ID
import wiles.shared.constants.Tokens.IF_ID
import wiles.shared.constants.Tokens.INT_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.LARGER_ID
import wiles.shared.constants.Tokens.LIST_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.MUTABLE_TYPE_ID
import wiles.shared.constants.Tokens.NEWLINE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.Tokens.RETURN_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.STRING_ID
import wiles.shared.constants.Tokens.TERMINATOR_ID
import wiles.shared.constants.Tokens.TIMES_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.Tokens.UNION_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
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
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK))))",
                DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK))))",
                NEWLINE_ID, NEWLINE_ID,
                DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK))))",
                DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID,
                NEWLINE_ID, PAREN_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun expressionsTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(!c)))))))",
                DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID,
                PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
                "!b", ASSIGN_ID, "!c", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(#3)))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID,
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
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID)
        assertResults(createExceptions(TokenExpectedException(END_OF_STATEMENT_EXPECTED_ERROR, NULL_LOCATION)),
            null,
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", PLUS_ID, "!b", DECLARE_ID, NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(END_BLOCK_ID), NULL_LOCATION)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID)
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(PAREN_END_ID), NULL_LOCATION)),
                null,
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_EXPRESSION_ERROR, NULL_LOCATION),TokenExpectedException(
            END_OF_STATEMENT_EXPECTED_ERROR, NULL_LOCATION)),"CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION, EXPRESSION(!a, %PLUS, !b))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", TIMES_ID, TIMES_ID, "!b", NEWLINE_ID, "!a", PLUS_ID, "!b",
            DECLARE_ID, "!c", ASSIGN_ID, "!d", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
        "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK))), EXPRESSION(!a, %PLUS, !b))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, CONTINUE_ID
            , NEWLINE_ID, "!a", PLUS_ID, "!b")
    }

    @Test
    fun methodTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, ANNOTATE_ID, INT_ID,
                START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), DECLARATION(TYPEDEF(TYPE: !int), !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID, PAREN_END_ID,
            ANNOTATE_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), DECLARATION(TYPEDEF(TYPE: !int), !a), DECLARATION(TYPEDEF(TYPE: !text), !b), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID,
                SEPARATOR_ID, "!b", ANNOTATE_ID, STRING_ID, PAREN_END_ID, ANNOTATE_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), DECLARATION(TYPEDEF(!nothing), !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, NOTHING_ID, PAREN_END_ID,
            ANNOTATE_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(!nothing))))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(EXPRESSION(!b), %ASSIGN, EXPRESSION(#3)))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID,
             "!b", ASSIGN_ID, "#3")
        assertResults(null, "CODE_BLOCK(DECLARATION(!product, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), DECLARATION(TYPEDEF(TYPE: !int), !a), DECLARATION(TYPEDEF(TYPE: !int), !b), CODE_BLOCK(EXPRESSION(EXPRESSION(!product), %ASSIGN, EXPRESSION(!a, %TIMES, !b)))))))",
            DECLARE_ID, "!product", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID,
                SEPARATOR_ID, "!b", ANNOTATE_ID, INT_ID, PAREN_END_ID, ANNOTATE_ID, INT_ID, NEWLINE_ID,
                DO_ID,  "!product", ASSIGN_ID, "!a", TIMES_ID, "!b")
        assertResults(null,"CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(DECLARATION(TYPEDEF(TYPE: !int), !args), CODE_BLOCK(EXPRESSION(!nothing))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!args", ANNOTATE_ID, INT_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(DECLARATION(!b, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(!nothing))))))))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, DECLARE_ID,
            "!b", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID, NEWLINE_ID, NEWLINE_ID)
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
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(TYPEDEF(TYPE: !int), CODE_BLOCK(RETURN(EXPRESSION(#10)))))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, ANNOTATE_ID, INT_ID,
            START_BLOCK_ID, NEWLINE_ID, RETURN_ID, "#10", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),null,
            RETURN_ID, "#10")
    }

    @Test
    fun declarationsTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPEDEF(TYPE: !int), !a, EXPRESSION(#10)))",
            DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, ASSIGN_ID, "#10")
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(ASSIGN_ID), NULL_LOCATION)),
            "CODE_BLOCK(DECLARATION(TYPEDEF(TYPE: !int), !a, EXPRESSION(#2)), DECLARATION(!a, EXPRESSION(#2)), DECLARATION(TYPEDEF(TYPE: !int), !a), DECLARATION(!a))",
            DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, NEWLINE_ID, DECLARE_ID, "!a")
    }

    @Test
    fun ifTest()
    {
        assertResults(null, "CODE_BLOCK(IF(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(!nothing))), EXPRESSION(!nothing))",
            IF_ID, TRUE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, NOTHING_ID)

        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(IF(EXPRESSION(!true), CODE_BLOCK(RETURN(EXPRESSION(#1))), %ELSE, CODE_BLOCK(RETURN(EXPRESSION(#2)))))))), IF(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(!nothing))), EXPRESSION(!nothing))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NEWLINE_ID,
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
        assertResults(null,"CODE_BLOCK(FOR(!i, %IN, EXPRESSION(!my_list), %FROM, EXPRESSION(#1), %TO, EXPRESSION(#100), CODE_BLOCK(EXPRESSION(!write_line, %APPLY, FUNC_CALL(EXPRESSION('hello!'))))))",
            FOR_ID, "!i", IN_ID, "!my_list", FROM_ID, "#1", TO_ID, "#100", NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, "!write_line", PAREN_START_ID, "@hello!", PAREN_END_ID, NEWLINE_ID, END_BLOCK_ID)
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
            "CODE_BLOCK(WHILE(EXPRESSION(!true), CODE_BLOCK(EXPRESSION(FUNC(CODE_BLOCK)), EXPRESSION(FUNC(CODE_BLOCK)))))",
            WHILE_ID, TRUE_ID, START_BLOCK_ID, NEWLINE_ID, DO_ID, BREAK_ID,
            NEWLINE_ID, DO_ID, CONTINUE_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun methodCallTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(!a, %PLUS, EXPRESSION(!b, %APPLY, FUNC_CALL)))",
            "!a", PLUS_ID, "!b", PAREN_START_ID, PAREN_END_ID)
        assertResults(null,"CODE_BLOCK(EXPRESSION(!min, %APPLY, FUNC_CALL(EXPRESSION(EXPRESSION(!my_list), %ASSIGN, EXPRESSION(!a)))))",
            "!min", PAREN_START_ID, "!my_list", ASSIGN_ID, "!a", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(!call, %APPLY, FUNC_CALL(EXPRESSION(EXPRESSION(!a), %ASSIGN, EXPRESSION(!b, %PLUS, !c)), EXPRESSION(!d), EXPRESSION(!e, %EQUALS, !f))))",
            "!call", PAREN_START_ID, "!a", ASSIGN_ID, "!b", PLUS_ID, "!c", SEPARATOR_ID, "!d", SEPARATOR_ID, "!e", EQUALS_ID, "!f", PAREN_END_ID)
    }

    @Test
    fun typesTest()
    {
        assertResults(null, "CODE_BLOCK(DECLARATION(TYPEDEF(TYPE: !func), !a))",
            DECLARE_ID, "!a", ANNOTATE_ID, FUNC_TYPE_ID, PAREN_START_ID, PAREN_END_ID)
        // let fn : func[a : int, b : string, : decimal]
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !func [1, 10, 1, 14] 
                        (
                            TYPEDEF
                            (
                                TYPE: !decimal [1, 36, 1, 41] 
                            ), 
                            DECLARATION
                            (
                                TYPEDEF
                                (
                                    TYPE: !int [1, 19, 1, 22] 
                                ), 
                                !a [1, 15, 1, 16]
                            ), 
                            DECLARATION
                            (
                                TYPEDEF
                                (
                                    TYPE: !text [1, 28, 1, 32] 
                                ), 
                                !b [1, 24, 1, 25]
                            )
                        )
                    ), 
                    !fn [1, 5, 1, 7]
                )
            )
        """, DECLARE_ID, "!fn", ANNOTATE_ID, FUNC_TYPE_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID, SEPARATOR_ID,
            "!b", ANNOTATE_ID, STRING_ID, SEPARATOR_ID, ANNOTATE_ID, DECIMAL_ID, PAREN_END_ID)

        /*
            let a : int?
            let b : int | nothing
            let c : either[int, nothing]
         */
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    TYPEDEF
                    (
                        %MAYBE [1, 12, 1, 13], 
                        TYPE: !int [1, 9, 1, 12] 
                    ), 
                    !a [1, 5, 1, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !int [2, 9, 2, 12] , 
                        %UNION [2, 13, 2, 15], 
                        !nothing [2, 16, 2, 23]
                    ), 
                    !b [2, 5, 2, 6]
                )
            )
        """,DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, MAYBE_ID, NEWLINE_ID,
            DECLARE_ID, "!b", ANNOTATE_ID, INT_ID, UNION_ID, NOTHING_ID, NEWLINE_ID)
    }

    @Test
    fun listTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(LIST(TYPEDEF(TYPE: !int), EXPRESSION(#1), EXPRESSION(#2), EXPRESSION(#3)))))",
            DECLARE_ID, "!a", ASSIGN_ID, BRACKET_START_ID,
            "#1", SEPARATOR_ID, "#2", SEPARATOR_ID, "#3", SEPARATOR_ID,
            BRACKET_END_ID, ANNOTATE_ID, INT_ID)
    }

    @Test
    fun typeLiteralsTest()
    {
        /*
              let my_TYPE := list[int | text]?
              let a : 1 + 2 * 3 := 123
         */
        assertResults(null,"CODE_BLOCK(DECLARATION(!my_type,EXPRESSION(%MAYBE,TYPE:!list(EXPRESSION(TYPE:!int,%UNION,TYPE:!text)))),DECLARATION(TYPEDEF(#1,%PLUS,EXPRESSION(#2,%TIMES,#3)),!a,EXPRESSION(#123)))",
            DECLARE_ID, "!my_type", ASSIGN_ID,
            LIST_ID, PAREN_START_ID, INT_ID, UNION_ID, STRING_ID, PAREN_END_ID, MAYBE_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, "#1", PLUS_ID, "#2", TIMES_ID, "#3", ASSIGN_ID, "#123")

        /*
                let a : int | text
                let a : list[anything]
                let a : list[7 + 8]
                let a : list[int | text]
                let a : list[int? | text]?
                let a : 17? := 17
                let a : true | false := 17
         */
        assertResults(null, """
            CODE_BLOCK
            (
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !int [1, 9, 1, 12] , 
                        %UNION [1, 13, 1, 15], 
                        TYPE: !text [1, 16, 1, 20] 
                    ), 
                    !a [1, 5, 1, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !list [2, 9, 2, 13] 
                        (
                            !anything [2, 14, 2, 17] 
                        )
                    ), 
                    !a [2, 5, 2, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !list [3, 9, 3, 13] 
                        (
                            EXPRESSION 
                            (
                                #7 [3, 14, 3, 15], 
                                %PLUS [3, 16, 3, 17], 
                                #8 [3, 18, 3, 19]
                            )
                        )
                    ), 
                    !a [3, 5, 3, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !list [4, 9, 4, 13] 
                        (
                            EXPRESSION 
                            (
                                TYPE: !int [4, 14, 4, 17] , 
                                %UNION [4, 18, 4, 20], 
                                TYPE: !text [4, 21, 4, 25] 
                            )
                        )
                    ), 
                    !a [4, 5, 4, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %MAYBE [5, 27, 5, 28], 
                        TYPE: !list [5, 9, 5, 13] 
                        (
                            EXPRESSION 
                            (
                                EXPRESSION
                                (
                                    %MAYBE [5, 17, 5, 18], 
                                    TYPE: !int [5, 14, 5, 17] 
                                ), 
                                %UNION [5, 19, 5, 21], 
                                TYPE: !text [5, 22, 5, 26] 
                            )
                        )
                    ), 
                    !a [5, 5, 5, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %MAYBE [6, 11, 6, 12], 
                        #17 [6, 9, 6, 11]
                    ), 
                    !a [6, 5, 6, 6], 
                    EXPRESSION
                    (
                        #17 [6, 16, 6, 18]
                    )
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        !true [7, 9, 7, 13], 
                        %UNION [7, 14, 7, 16], 
                        !false [7, 17, 7, 22]
                    ), 
                    !a [7, 5, 7, 6], 
                    EXPRESSION
                    (
                        #17 [7, 26, 7, 28]
                    )
                )
            )
        """, DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, UNION_ID, STRING_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, LIST_ID, PAREN_START_ID, ANYTHING_ID, PAREN_END_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, LIST_ID, PAREN_START_ID, "#7", PLUS_ID, "#8", PAREN_END_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, LIST_ID, PAREN_START_ID, INT_ID, UNION_ID, STRING_ID, PAREN_END_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, LIST_ID, PAREN_START_ID, INT_ID, MAYBE_ID, UNION_ID, STRING_ID, PAREN_END_ID, MAYBE_ID, NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, "#17", MAYBE_ID, ASSIGN_ID, "#17", NEWLINE_ID,
            DECLARE_ID, "!a", ANNOTATE_ID, TRUE_ID, UNION_ID, FALSE_ID, ASSIGN_ID, "#17", NEWLINE_ID)

        // let def const truth := true | false
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION: GLOBAL; CONST 
                (
                    !truth [1, 15, 1, 20], 
                    EXPRESSION
                    (
                        !true [1, 24, 1, 28], 
                        %UNION [1, 29, 1, 30], 
                        !false [1, 31, 1, 36]
                    )
                )
            )
        """, DECLARE_ID, GLOBAL_ID, CONST_ID, "!truth", ASSIGN_ID, TRUE_ID, UNION_ID, FALSE_ID)
    }

    @Test
    fun constTest(){
        //let const a : const[int] := 123
        assertResults(null,"""
        CODE_BLOCK(
            DECLARATION: CONST 
            (
                TYPEDEF(
                    TYPE: CONST(
                        TYPE: !int
                    )
                ),
                !a [1, 11, 1, 12], 
                EXPRESSION
                (
                    #123 [1, 29, 1, 32]
                )
            )
        )
""", DECLARE_ID, CONST_ID, "!a", ANNOTATE_ID, CONST_ID, PAREN_START_ID, INT_ID, PAREN_END_ID, ASSIGN_ID, "#123")

        //let const var a : const[int] := 123
        assertResults(createExceptions(UnexpectedTokenException(CONST_CANT_BE_VAR_ERROR, NULL_LOCATION)),
            """
                CODE_BLOCK
                (
                    DECLARATION
                )
        """, DECLARE_ID, CONST_ID, VARIABLE_ID, "!a",
            ANNOTATE_ID, CONST_ID, PAREN_START_ID, INT_ID, PAREN_END_ID, ASSIGN_ID, "#123")
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
                        TYPEDEF(TYPE: !int) [1, 16, 1, 19] , 
                        !a [1, 12, 1, 13]
                    )
                )
            """, DECLARE_ID, GLOBAL_ID, "!a", ANNOTATE_ID, INT_ID)
    }

    @Test
    fun textTest()
    {
        // write_line("It's a beautiful day! but i still feel sad :-\b; sorry")
        assertResults(null,"CODE_BLOCK(EXPRESSION(!write_line, %APPLY, FUNC_CALL(EXPRESSION('It\\qs\\sa\\sbeautiful\\sday!\\sbut\\si\\sstill\\sfeel\\ssad\\s:-\\b\\ssorry\\s-_-\\noh\\swell'))))",
            "!write_line", PAREN_START_ID, "@It's a beautiful day! but i still feel sad :-\\ sorry -_-\noh well", PAREN_END_ID, TERMINATOR_ID)
    }

    @Test
    fun dictTest(){
        TODO("Add tests")
    }
    @Test
    fun dataTest(){
        TODO("Add tests")
    }

    @Test
    fun mutableTest()
    {
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    !a [1, 5, 1, 6], 
                    EXPRESSION
                    (
                        LIST [1, 14, 1, 15]
                        (
                            EXPRESSION
                            (
                                #123 [1, 11, 1, 14]
                            )
                        )
                    )
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        TYPE: !mutable [2, 9, 2, 16] 
                        (
                            TYPE: !list [2, 17, 2, 21] 
                            (
                                TYPE: !int [2, 22, 2, 25] 
                            )
                        )
                    ), 
                    !b [2, 5, 2, 6], 
                    EXPRESSION
                    (
                        %MUTABLE [2, 31, 2, 32], 
                        !a [2, 32, 2, 33]
                    )
                )
            )
        """, DECLARE_ID, "!a", ASSIGN_ID, BRACKET_START_ID, "#123", BRACKET_END_ID, NEWLINE_ID,
            DECLARE_ID, "!b", ANNOTATE_ID, MUTABLE_TYPE_ID, PAREN_START_ID, LIST_ID, PAREN_START_ID, INT_ID,
            PAREN_END_ID, PAREN_END_ID, ASSIGN_ID, MUTABLE_ID, "!a")
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