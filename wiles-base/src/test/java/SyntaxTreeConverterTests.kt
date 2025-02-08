
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
import wiles.shared.constants.Tokens.CONST_TYPE_ID
import wiles.shared.constants.Tokens.CONTINUE_ID
import wiles.shared.constants.Tokens.DATA_END_ID
import wiles.shared.constants.Tokens.DATA_START_ID
import wiles.shared.constants.Tokens.DECIMAL_ID
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.Tokens.DICT_END_ID
import wiles.shared.constants.Tokens.DICT_START_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.FOR_ID
import wiles.shared.constants.Tokens.FUNC_ID
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
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.Tokens.UNION_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.constants.Tokens.WHILE_ID
import wiles.shared.constants.Tokens.YIELDS_ID
import wiles.shared.constants.Utils.NULL_LOCATION

@Testable
class SyntaxTreeConverterTests {
    private fun trimAll(string: String) : String
    {
        return string.replace("[\\r\\n ]|\\[.*]".toRegex(),"")
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
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(%ASSIGN, !b, !c))))))",
                DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID,
                PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
                "!b", ASSIGN_ID, "!c", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(%ASSIGN, !b, #3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID,
            PAREN_START_ID, PAREN_END_ID, START_BLOCK_ID, TERMINATOR_ID,
            "!b", ASSIGN_ID, "#3", TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%MINUS, EXPRESSION(%PLUS, !b, #3), #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,!a,!b),EXPRESSION(%UNARY_PLUS,!c),EXPRESSION(%PLUS,EXPRESSION(%PLUS,!a,!b),!c))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c")
        assertResults(null, "CODE_BLOCK(EXPRESSION(%MINUS, EXPRESSION(%PLUS, !b, #3), #5))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5")
        assertResults(null, "CODE_BLOCK(EXPRESSION(%ASSIGN,!c,EXPRESSION(%PLUS,EXPRESSION(%UNARY_MINUS,#10),EXPRESSION(%UNARY_PLUS,EXPRESSION(%UNARY_PLUS,#10)))))",
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
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,!a,EXPRESSION(%PLUS,EXPRESSION(%PLUS,!b,!c),!d)))",
                "!a", PLUS_ID, PAREN_START_ID, PAREN_START_ID, "!b", PLUS_ID, "!c",
                PAREN_END_ID, PLUS_ID, "!d", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,!a,EXPRESSION(%PLUS,!b,!c)))",
                "!a", PLUS_ID, PAREN_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,!a,EXPRESSION(%PLUS,!b,!c)))",
                "!a", PLUS_ID, NEWLINE_ID, PAREN_START_ID, "!b", PLUS_ID, "!c", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,!a,!b))",
            PAREN_START_ID, "!a", PLUS_ID, "!b", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%PLUS,EXPRESSION(%PLUS,!a,!b),!c))",
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
            END_OF_STATEMENT_EXPECTED_ERROR, NULL_LOCATION)),"CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION, EXPRESSION(%PLUS, !a, !b))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID,
            START_BLOCK_ID, NEWLINE_ID, "!a", TIMES_ID, TIMES_ID, "!b", NEWLINE_ID, "!a", PLUS_ID, "!b",
            DECLARE_ID, "!c", ASSIGN_ID, "!d", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
        "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK))), EXPRESSION(%PLUS, !a, !b))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, CONTINUE_ID
            , NEWLINE_ID, "!a", PLUS_ID, "!b")
    }

    @Test
    fun methodTest() {
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(!int), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, YIELDS_ID, INT_ID,
                START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(!int), DECLARATION(TYPEDEF(!int), !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID, PAREN_END_ID,
            YIELDS_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(!int), DECLARATION(TYPEDEF(!int), !a), DECLARATION(TYPEDEF(!text), !b), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID,
                SEPARATOR_ID, "!b", ANNOTATE_ID, STRING_ID, PAREN_END_ID, YIELDS_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(TYPEDEF(!int), DECLARATION(TYPEDEF(!nothing), !a), CODE_BLOCK))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, NOTHING_ID, PAREN_END_ID,
            YIELDS_ID, INT_ID, START_BLOCK_ID, TERMINATOR_ID, END_BLOCK_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(!nothing)))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null, "CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(CODE_BLOCK(EXPRESSION(%ASSIGN, !b, #3))))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID,
             "!b", ASSIGN_ID, "#3")
        assertResults(null, "CODE_BLOCK(DECLARATION(!product, EXPRESSION(FUNC(TYPEDEF(!int), DECLARATION(TYPEDEF(!int), !a), DECLARATION(TYPEDEF(!int), !b), CODE_BLOCK(EXPRESSION(%ASSIGN, !product, EXPRESSION(%TIMES, !a, !b)))))))",
            DECLARE_ID, "!product", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID,
                SEPARATOR_ID, "!b", ANNOTATE_ID, INT_ID, PAREN_END_ID, YIELDS_ID, INT_ID, NEWLINE_ID,
                DO_ID,  "!product", ASSIGN_ID, "!a", TIMES_ID, "!b")
        assertResults(null,"CODE_BLOCK(DECLARATION(!main, EXPRESSION(FUNC(DECLARATION(TYPEDEF(!int), !args), CODE_BLOCK(!nothing)))))",
            DECLARE_ID, "!main", ASSIGN_ID, FUNC_ID, PAREN_START_ID, "!args", ANNOTATE_ID, INT_ID, PAREN_END_ID, DO_ID, NOTHING_ID)
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(DECLARATION(!b, EXPRESSION(FUNC(CODE_BLOCK(!nothing)))))))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, DECLARE_ID,
            "!b", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID, NEWLINE_ID, NEWLINE_ID)
    }

    @Test
    fun orderOfOperationsTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(%OR,!a,EXPRESSION(%LARGER,EXPRESSION(%PLUS,!b,!c),!d)))",
            "!a", OR_ID, "!b", PLUS_ID, "!c", LARGER_ID, "!d")

        assertResults(null,"CODE_BLOCK(EXPRESSION(%NOT,EXPRESSION(%EQUALS,EXPRESSION(%PLUS,!a,!b),#10)))",
        NOT_ID,"!a", PLUS_ID,"!b", EQUALS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(%PLUS,EXPRESSION(%TIMES,!a,EXPRESSION(%POWER,!b,#2)),#10))",
            "!a", TIMES_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(%PLUS,EXPRESSION(%POWER,!a,EXPRESSION(%POWER,!b,#2)),#10))",
            "!a", POWER_ID, "!b", POWER_ID, "#2", PLUS_ID, "#10")

        assertResults(null,"CODE_BLOCK(EXPRESSION(%OR,EXPRESSION(%NOT,EXPRESSION(%NOT,EXPRESSION(%EQUALS,!a,!b))),!c))",
            NOT_ID, NOT_ID, "!a", EQUALS_ID, "!b", OR_ID, "!c")
    }

    @Test
    fun returnTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(TYPEDEF(!int), CODE_BLOCK(RETURN(#10))))))",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, YIELDS_ID, INT_ID,
            START_BLOCK_ID, NEWLINE_ID, RETURN_ID, "#10", NEWLINE_ID, END_BLOCK_ID)
        assertResults(createExceptions(UnexpectedTokenException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),null,
            RETURN_ID, "#10")
    }

    @Test
    fun declarationsTest()
    {
        assertResults(null,"CODE_BLOCK(DECLARATION(TYPEDEF(!int), !a, #10))",
            DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, ASSIGN_ID, "#10")
        assertResults(createExceptions(UnexpectedEndException(TOKEN_EXPECTED_ERROR.format(ASSIGN_ID), NULL_LOCATION)),
            "CODE_BLOCK(DECLARATION(TYPEDEF(!int), !a, #2), DECLARATION(!a, #2), DECLARATION(TYPEDEF(!int), !a), DECLARATION(!a))",
            DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", ASSIGN_ID, "#2", NEWLINE_ID, DECLARE_ID, "!a", ANNOTATE_ID, INT_ID, NEWLINE_ID, DECLARE_ID, "!a")
    }

    @Test
    fun ifTest()
    {
        assertResults(null, "CODE_BLOCK(IF(!true, CODE_BLOCK(!nothing)), !nothing)",
            IF_ID, TRUE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, NOTHING_ID)

        assertResults(null, "CODE_BLOCK(DECLARATION(!a, EXPRESSION(FUNC(CODE_BLOCK(IF(!true, CODE_BLOCK(RETURN(#1)), %ELSE, CODE_BLOCK(RETURN(#2))))))), IF(!true, CODE_BLOCK(!nothing)), !nothing)",
            DECLARE_ID, "!a", ASSIGN_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID, DO_ID, NEWLINE_ID,
            IF_ID, START_BLOCK_ID, TRUE_ID, DO_ID, RETURN_ID, "#1", NEWLINE_ID,
            ELSE_ID, DO_ID, RETURN_ID, "#2", NEWLINE_ID, END_BLOCK_ID, NEWLINE_ID,
            IF_ID, TRUE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, NOTHING_ID)

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(%LARGER, !a, #10), CODE_BLOCK(!nothing), EXPRESSION(%LARGER, !a, #0), CODE_BLOCK(!nothing), %ELSE, CODE_BLOCK(!nothing)))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID, "!a", LARGER_ID, "#0",
            DO_ID, NOTHING_ID, NEWLINE_ID, ELSE_ID, DO_ID, NOTHING_ID, NEWLINE_ID, END_BLOCK_ID)

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(%LARGER, !a, #0), CODE_BLOCK(!nothing), %ELSE, CODE_BLOCK(IF(EXPRESSION(%LARGER, !a, #10), CODE_BLOCK(!nothing)))), EXPRESSION(%ASSIGN, !a, !b))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#0", DO_ID, NOTHING_ID, NEWLINE_ID,
            ELSE_ID, DO_ID, IF_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID,NEWLINE_ID, END_BLOCK_ID,
            NEWLINE_ID, "!a", ASSIGN_ID, "!b")

        assertResults(null,"CODE_BLOCK(IF(EXPRESSION(%LARGER, !a, #10), CODE_BLOCK(!nothing), EXPRESSION(%LARGER, !a, #0), CODE_BLOCK(!nothing)))",
            IF_ID, START_BLOCK_ID, NEWLINE_ID, "!a", LARGER_ID, "#10", DO_ID, NOTHING_ID, NEWLINE_ID,
            "!a", LARGER_ID, "#0", DO_ID, NOTHING_ID, NEWLINE_ID,NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun forTest()
    {
        assertResults(null,"CODE_BLOCK(FOR(!i, %IN, !my_list, CODE_BLOCK(EXPRESSION(%APPLY, !write_line, FUNC_CALL('hello!')))))",
            FOR_ID, "!i", IN_ID, "!my_list", NEWLINE_ID, START_BLOCK_ID, NEWLINE_ID, "!write_line", PAREN_START_ID, "@hello!", PAREN_END_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun whileTest()
    {
        assertResults(null,"CODE_BLOCK(WHILE(!true, CODE_BLOCK(CONTINUE)))",
            WHILE_ID, TRUE_ID, START_BLOCK_ID, NEWLINE_ID, CONTINUE_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun breakContinueErrorTest()
    {
        assertResults(null,"CODE_BLOCK(WHILE(!true, CODE_BLOCK(BREAK)))",
            WHILE_ID, TRUE_ID, DO_ID, BREAK_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK", BREAK_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK", CONTINUE_ID)
        assertResults(createExceptions(TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION),
                                       TokenExpectedException(INVALID_STATEMENT_ERROR, NULL_LOCATION)),
            "CODE_BLOCK(WHILE(!true, CODE_BLOCK(EXPRESSION(FUNC(CODE_BLOCK)), EXPRESSION(FUNC(CODE_BLOCK)))))",
            WHILE_ID, TRUE_ID, START_BLOCK_ID, NEWLINE_ID, DO_ID, BREAK_ID,
            NEWLINE_ID, DO_ID, CONTINUE_ID, NEWLINE_ID, END_BLOCK_ID)
    }

    @Test
    fun methodCallTest()
    {
        assertResults(null,"CODE_BLOCK(EXPRESSION(%PLUS, !a, EXPRESSION(%APPLY, !b, FUNC_CALL)))",
            "!a", PLUS_ID, "!b", PAREN_START_ID, PAREN_END_ID)
        assertResults(null,"CODE_BLOCK(EXPRESSION(%APPLY, !min, FUNC_CALL(EXPRESSION(%ASSIGN, EXPRESSION(!my_list), !a))))",
            "!min", PAREN_START_ID, "!my_list", ASSIGN_ID, "!a", PAREN_END_ID)
        assertResults(null, "CODE_BLOCK(EXPRESSION(%APPLY, !call, FUNC_CALL(EXPRESSION(%ASSIGN, EXPRESSION(!a), EXPRESSION(%PLUS, !b, !c)), !d, EXPRESSION(%EQUALS, !e, !f))))",
            "!call", PAREN_START_ID, "!a", ASSIGN_ID, "!b", PLUS_ID, "!c", SEPARATOR_ID, "!d", SEPARATOR_ID, "!e", EQUALS_ID, "!f", PAREN_END_ID)
    }

    @Test
    fun typesTest()
    {
        // let a : fun()
        assertResults(null, "CODE_BLOCK(DECLARATION(TYPEDEF(FUNC: !type), !a))",
            DECLARE_ID, "!a", ANNOTATE_ID, FUNC_ID, PAREN_START_ID, PAREN_END_ID)
        // let fn : func[a : int, b : string] -> decimal
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    TYPEDEF
                    (
                        FUNC:!type
                        (
                            TYPEDEF
                            (
                                !decimal [1, 36, 1, 41] 
                            ), 
                            DECLARATION
                            (
                                TYPEDEF
                                (
                                    !int [1, 19, 1, 22] 
                                ), 
                                !a [1, 15, 1, 16]
                            ), 
                            DECLARATION
                            (
                                TYPEDEF
                                (
                                    !text [1, 28, 1, 32] 
                                ), 
                                !b [1, 24, 1, 25]
                            )
                        )
                    ), 
                    !fn [1, 5, 1, 7]
                )
            )
        """, DECLARE_ID, "!fn", ANNOTATE_ID, FUNC_ID, PAREN_START_ID, "!a", ANNOTATE_ID, INT_ID, SEPARATOR_ID,
            "!b", ANNOTATE_ID, STRING_ID, PAREN_END_ID, YIELDS_ID, DECIMAL_ID)

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
                        !int [1, 9, 1, 12] 
                    ), 
                    !a [1, 5, 1, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %UNION [2, 13, 2, 15], 
                        !int [2, 9, 2, 12] , 
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
        assertResults(null,"CODE_BLOCK(DECLARATION(!a, EXPRESSION(LIST(TYPEDEF(!int), #1, #2, #3))))",
            DECLARE_ID, "!a", ASSIGN_ID, BRACKET_START_ID,
            "#1", SEPARATOR_ID, "#2", SEPARATOR_ID, "#3", SEPARATOR_ID,
            BRACKET_END_ID, ANNOTATE_ID, INT_ID)
    }

    @Test
    fun typeLiteralsTest()
    {
        /*
              let my_type := list[int | text]?
              let a : 1 + 2 * 3 := 123
         */
        assertResults(null,"""
CODE_BLOCK
(
    DECLARATION
    (
        !my_type [1, 5, 1, 12], 
        EXPRESSION
        (
            %MAYBE [1, 32, 1, 33], 
            EXPRESSION
            (
                %APPLY [1, 20, 1, 21], 
                !list [1, 16, 1, 20], 
                FUNC_CALL
                (
                    EXPRESSION
                    (
                        %UNION [1, 25, 1, 26], 
                        !int [1, 21, 1, 24], 
                        !text [1, 27, 1, 31]
                    )
                )
            )
        )
    ), 
    DECLARATION
    (
        TYPEDEF
        (
            %PLUS [2, 11, 2, 12], 
            #1 [2, 9, 2, 10], 
            EXPRESSION
            (
                %TIMES [2, 15, 2, 16], 
                #2 [2, 13, 2, 14],  
                #3 [2, 17, 2, 18]
            )
        ), 
        !a [2, 5, 2, 6], 
        #123 [2, 22, 2, 25]
    )
)
        """,
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
                        %UNION [1, 13, 1, 14], 
                        !int [1, 9, 1, 12], 
                        !text [1, 15, 1, 19]
                    ), 
                    !a [1, 5, 1, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %APPLY [2, 13, 2, 14], 
                        !list [2, 9, 2, 13], 
                        FUNC_CALL
                        (
                            !anything [2, 14, 2, 22]
                        )
                    ), 
                    !a [2, 5, 2, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %APPLY [3, 13, 3, 14], 
                        !list [3, 9, 3, 13], 
                        FUNC_CALL
                        (
                            EXPRESSION
                            (
                                %PLUS [3, 16, 3, 17], 
                                #7 [3, 14, 3, 15], 
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
                        %APPLY [4, 13, 4, 14], 
                        !list [4, 9, 4, 13], 
                        FUNC_CALL
                        (
                            EXPRESSION
                            (
                                %UNION [4, 18, 4, 19], 
                                !int [4, 14, 4, 17], 
                                !text [4, 20, 4, 24]
                            )
                        )
                    ), 
                    !a [4, 5, 4, 6]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %MAYBE [5, 26, 5, 27], 
                        EXPRESSION
                        (
                            %APPLY [5, 13, 5, 14], 
                            !list [5, 9, 5, 13], 
                            FUNC_CALL
                            (
                                EXPRESSION
                                (
                                    %UNION [5, 19, 5, 20], 
                                    EXPRESSION
                                    (
                                        %MAYBE [5, 17, 5, 18], 
                                        !int [5, 14, 5, 17]
                                    ), 
                                    !text [5, 21, 5, 25]
                                )
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
                    #17 [6, 16, 6, 18]
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %UNION [7, 14, 7, 15], 
                        !true [7, 9, 7, 13], 
                        !false [7, 16, 7, 21]
                    ), 
                    !a [7, 5, 7, 6], 
                    #17 [7, 25, 7, 27]
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
                        %UNION [1, 29, 1, 30], 
                        !true [1, 24, 1, 28], 
                        !false [1, 31, 1, 36]
                    )
                )
            )
        """, DECLARE_ID, GLOBAL_ID, CONST_ID, "!truth", ASSIGN_ID, TRUE_ID, UNION_ID, FALSE_ID)
    }

    @Test
    fun constTest(){
        //let const a : constant(int) := 123
        assertResults(null,"""
        CODE_BLOCK(
            DECLARATION: CONST
            (
                TYPEDEF(
                    %APPLY,
                    !constant,
                    FUNC_CALL(!int)
                ),
                !a [1, 11, 1, 12], 
                #123 [1, 29, 1, 32]
            )
        )
""", DECLARE_ID, CONST_ID, "!a", ANNOTATE_ID, CONST_TYPE_ID, PAREN_START_ID, INT_ID, PAREN_END_ID, ASSIGN_ID, "#123")

        //let const var a : constant(int) := 123
        assertResults(createExceptions(UnexpectedTokenException(CONST_CANT_BE_VAR_ERROR, NULL_LOCATION)),
            """
                CODE_BLOCK
                (
                    DECLARATION
                )
        """, DECLARE_ID, CONST_ID, VARIABLE_ID, "!a",
            ANNOTATE_ID, CONST_TYPE_ID, PAREN_START_ID, INT_ID, PAREN_END_ID, ASSIGN_ID, "#123")
    }

    @Test
    fun globalDeclarationTest()
    {
        //def a := 234
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION: GLOBAL
                (
                    !a [1, 12, 1, 13], 
                    #234 [1, 17, 1, 20]
                )
            )
        """, GLOBAL_ID, "!a", ASSIGN_ID, "#234")

        //let def a : int
        assertResults(createExceptions(TokenExpectedException(EXPECTED_GLOBAL_VALUE_ERROR, NULL_LOCATION)),
            """
                CODE_BLOCK
                (
                    DECLARATION: GLOBAL
                    (
                        TYPEDEF(!int) [1, 16, 1, 19] , 
                        !a [1, 12, 1, 13]
                    )
                )
            """, DECLARE_ID, GLOBAL_ID, "!a", ANNOTATE_ID, INT_ID)

    }

    @Test
    fun textTest()
    {
        // write_line("It's a beautiful day! but i still feel sad :-\b sorry -__\n;oh well")
        assertResults(null,"CODE_BLOCK(EXPRESSION(%APPLY, !write_line, FUNC_CALL('It\\as\\wa\\wbeautiful\\wday!\\wbut\\wi\\wstill\\wfeel\\wsad\\w:-\\b\\wsorry\\w-_-\\noh\\wwell')))",
            "!write_line", PAREN_START_ID, "@It's a beautiful day! but i still feel sad :-\\ sorry -_-\noh well", PAREN_END_ID, TERMINATOR_ID)
    }

    @Test
    fun dictTest(){
        // let a := {1 -> "hi", 2 -> "bye!"} : int -> text
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    !a [1, 5, 1, 6], 
                    EXPRESSION
                    (
                        DICT [1, 33, 1, 34]
                        (
                            TYPEDEF: KEY
                            (
                                !int [1, 37, 1, 40]
                            ), 
                            TYPEDEF: VALUE
                            (
                                !text [1, 44, 1, 48]
                            ), 
                            #1, 
                            'hi', 
                            #2, 
                            'bye!'
                        )
                    )
                )
            )
        """, DECLARE_ID, "!a", ASSIGN_ID, DICT_START_ID, "#1", YIELDS_ID, "@hi", SEPARATOR_ID,
            "#2", YIELDS_ID, "@bye!", DICT_END_ID, ANNOTATE_ID, INT_ID, YIELDS_ID, STRING_ID)

        /*
        {} : int -> text
        {} : int
        {} -> text
        {}
         */
        assertResults(null,"""
            CODE_BLOCK
            (
                EXPRESSION
                (
                    DICT [1, 2, 1, 3]
                    (
                        TYPEDEF: KEY 
                        (
                            !int [1, 6, 1, 9]
                        ), 
                        TYPEDEF: VALUE 
                        (
                            !text [1, 13, 1, 17]
                        )
                    )
                ), 
                EXPRESSION
                (
                    DICT [2, 2, 2, 3]
                    (
                        TYPEDEF: KEY 
                        (
                            !int [2, 6, 2, 9]
                        )
                    )
                ), 
                EXPRESSION
                (
                    DICT [3, 2, 3, 3]
                    (
                        TYPEDEF: VALUE 
                        (
                            !text [3, 7, 3, 11]
                        )
                    )
                ), 
                EXPRESSION
                (
                    DICT [4, 2, 4, 3]
                )
            )
""",
            DICT_START_ID, DICT_END_ID, ANNOTATE_ID, INT_ID, YIELDS_ID, STRING_ID, NEWLINE_ID,
            DICT_START_ID, DICT_END_ID, ANNOTATE_ID, INT_ID, NEWLINE_ID,
            DICT_START_ID, DICT_END_ID, YIELDS_ID, STRING_ID, NEWLINE_ID,
            DICT_START_ID, DICT_END_ID)
    }

    @Test
    fun dataTest(){
        // let a := << name := "alex", age  := 26 >>
        assertResults(null,"""
            CODE_BLOCK
            (
                DECLARATION
                (
                    !a [1, 5, 1, 6], 
                    EXPRESSION
                    (
                        DATA [1, 40, 1, 42]
                        (
                            DECLARATION
                            (
                                !name [1, 13, 1, 17], 
                                'alex'
                            ), 
                            DECLARATION
                            (
                                !age [1, 29, 1, 32], 
                                #26
                            )
                        )
                    )
                )
            )""", DECLARE_ID, "!a", ASSIGN_ID, DATA_START_ID, "!name", ASSIGN_ID, "@alex", SEPARATOR_ID,
            "!age", ASSIGN_ID, "#26", DATA_END_ID)
    }

    @Test
    fun mutableTest()
    {
        /*
            let a := [123]
            let b : mutable(list(int)) := a
         */
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
                            #123 [1, 11, 1, 14]
                        )
                    )
                ), 
                DECLARATION
                (
                    TYPEDEF
                    (
                        %APPLY [2, 16, 2, 17], 
                        !mutable [2, 9, 2, 16], 
                        FUNC_CALL
                        (
                            EXPRESSION
                            (
                                %APPLY [2, 21, 2, 22], 
                                !list [2, 17, 2, 21], 
                                FUNC_CALL
                                (
                                    !int [2, 22, 2, 25]
                                )
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

    @Test
    fun innerMethodTest()
    {
        //(begin; nothing; end)
        assertResults(null,"""
            CODE_BLOCK
            (
                EXPRESSION
                (
                    FUNC [1, 2, 1, 7]
                    (
                        CODE_BLOCK
                        (
                            !nothing [1, 9, 1, 16]
                        )
                    )
                )
            )""",
            PAREN_START_ID, START_BLOCK_ID, TERMINATOR_ID, NOTHING_ID, TERMINATOR_ID, END_BLOCK_ID, PAREN_END_ID)
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