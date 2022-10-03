import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static in.costea.wiles.statics.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class SyntaxTreeConverterTests
{

    private static class CreateConverter
    {
        TokensToSyntaxTreeConverter converter;
        CompilationExceptionsCollection exceptions;
        AbstractCommand tree;

        public CreateConverter(String... tokens)
        {
            converter = new TokensToSyntaxTreeConverter(Arrays.stream(tokens).map(Token::new).toList());
            tree = converter.convert();
            exceptions = converter.getExceptions();
        }

        public CompilationExceptionsCollection getExceptions()
        {
            return exceptions;
        }

        public String getResult()
        {
            return tree.toString();
        }
    }

    private void assertResults(CompilationExceptionsCollection exceptions, String expectedResult, String... tokens)
    {
        CreateConverter converter = new CreateConverter(tokens);
        if (exceptions == null)
            exceptions = new CompilationExceptionsCollection();
        assertEquals(exceptions, converter.getExceptions());
        if (expectedResult != null)
            assertEquals(expectedResult, converter.getResult());
    }

    private CompilationExceptionsCollection createExceptions(AbstractCompilationException... list)
    {
        CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
        exceptions.add(list);
        return exceptions;
    }

    @Test
    public void newlineTests()
    {
        assertResults(null, "PROGRAM(METHOD a(TYPE NOTHING; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a(TYPE NOTHING; CODE_BLOCK))",
                NEWLINE_ID, NEWLINE_ID, DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a(TYPE NOTHING; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID,
                NEWLINE_ID, ROUND_BRACKET_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID);
    }

    @Test
    public void expressionsTest()
    {
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!b; ASSIGN; !c))))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, "!b", ASSIGN_ID, "!c", END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!b; ASSIGN; #3))))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, "!b", ASSIGN_ID, "#3", END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!b; PLUS; #3; MINUS; #5))))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5");

        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; PLUS; !b); EXPRESSION(#0; PLUS; !c); EXPRESSION(!a; PLUS; !b; PLUS; !c))))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c");

        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!b; PLUS; #3; MINUS; #5))))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5");

        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!c; ASSIGN; #0; MINUS; #10; PLUS; #0; PLUS; EXPRESSION ROUND(#0; PLUS; #10)))))",
                "!c", ASSIGN_ID, MINUS_ID, "#10", PLUS_ID, NEWLINE_ID, PLUS_ID, ROUND_BRACKET_START_ID, PLUS_ID, "#10", ROUND_BRACKET_END_ID);

    }

    @Test
    public void expressionsTestException()
    {
        assertResults(createExceptions(new UnexpectedEndException("Expression unfinished!", null)),
                null,
                "!a", PLUS_ID, "!b", PLUS_ID);

        assertResults(createExceptions(new TokenExpectedException("Identifier or unary operator expected!", null)),
                null,
                "!b", PLUS_ID, TIMES_ID, "#5");

        assertResults(createExceptions(new UnexpectedTokenException("*", null)),
                null,
                TIMES_ID, "!a");

        assertResults(createExceptions(new TokenExpectedException("Identifier or unary operator expected!",null),
                        new UnexpectedTokenException(")",null)),
                null,
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, "BREAK", ROUND_BRACKET_END_ID);
    }

    @Test
    public void parenthesesTests()
    {
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION ROUND(EXPRESSION ROUND(!b; PLUS; !c); PLUS; !d)))))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c",
                ROUND_BRACKET_END_ID, PLUS_ID, "!d", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION ROUND(!b; PLUS; !c)))))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; PLUS; EXPRESSION ROUND(!b; PLUS; !c)))))",
                "!a", PLUS_ID, NEWLINE_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION ROUND(!a; PLUS; !b))))",
                ROUND_BRACKET_START_ID, "!a", PLUS_ID, "!b", ROUND_BRACKET_END_ID);
    }

    @Test
    public void programExceptionsTest()
    {
        assertResults(createExceptions(new TokenExpectedException("Token \"method\" expected!", null)),
                null,
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, END_BLOCK_ID, "!b");

        assertResults(createExceptions(new TokenExpectedException("Token \"begin\" expected!", null),
                        new TokenExpectedException("Token \"method\" expected!", null)),
                null,
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID,
                END_BLOCK_ID, NEWLINE_ID, "!c", "!d", NEWLINE_ID, "!e");

        assertResults(createExceptions(new UnexpectedEndException("Token \"end\" expected!", null)),
                null,
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID);

        assertResults(createExceptions(new UnexpectedEndException("Token \")\" expected!", null)),
                null,
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID);

        assertResults(createExceptions(new UnexpectedEndException("Expected method name!", null)),
                null,
                DECLARE_METHOD_ID);

        assertResults(createExceptions(new UnexpectedEndException("Token \"begin\" expected!", null),
                        new UnexpectedEndException("Token \"method\" expected!", null)),
                null,
                DECLARE_METHOD_ID, "!name", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, END_BLOCK_ID, STATEMENT_TERMINATOR_ID,
                DECLARE_METHOD_ID, "!name", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, STATEMENT_TERMINATOR_ID);

        assertResults(createExceptions(new UnexpectedTokenException("Cannot declare method in body-only mode!", null)),
                null,
                "!a", PLUS_ID, "!b", NEWLINE_ID,
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID, END_BLOCK_ID);
    }

    @Test
    public void methodTest()
    {
        assertResults(null, "PROGRAM(METHOD main(TYPE INT32; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, COLON_ID, "!int",
                START_BLOCK_ID, END_BLOCK_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE INT32; DECLARATION(!a; TYPE INT32); CODE_BLOCK))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID,"!a", COLON_ID,"!int",ROUND_BRACKET_END_ID,
                COLON_ID, "!int", START_BLOCK_ID, END_BLOCK_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE INT32; DECLARATION(!a; TYPE INT32); DECLARATION(!b; TYPE STRING); CODE_BLOCK))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID,"!a", COLON_ID,"!int",
                "COMMA","!b",COLON_ID,"!text", ROUND_BRACKET_END_ID, COLON_ID, "!int", START_BLOCK_ID, END_BLOCK_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE INT32; DECLARATION(!a; TYPE NOTHING); CODE_BLOCK))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID,"!a", COLON_ID,NOTHING_ID,ROUND_BRACKET_END_ID,
                COLON_ID, "!int", START_BLOCK_ID, END_BLOCK_ID);
        assertResults(null,"PROGRAM(METHOD a(TYPE NOTHING; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, DO_ID, NOTHING_ID);
        assertResults(null, "PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!b; ASSIGN; #3))))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                ,DO_ID, "!b", ASSIGN_ID, "#3");
        assertResults(null,"PROGRAM(METHOD product(TYPE INT64; DECLARATION(!a; TYPE INT32); DECLARATION(!b; TYPE INT32); CODE_BLOCK(EXPRESSION(!product; ASSIGN; !a; TIMES; !b))))",
                DECLARE_METHOD_ID, "!product", ROUND_BRACKET_START_ID, "!a", COLON_ID, "!int",
                COMMA_ID, "!b", COLON_ID, "!int", ROUND_BRACKET_END_ID, COLON_ID, "!bigint", NEWLINE_ID,
                DO_ID, "!product", ASSIGN_ID, "!a", TIMES_ID, "!b");

    }

    @Test
    public void squareParenthesesTest()
    {
        assertResults(null,"PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; EXPRESSION SQUARE(!b; COMMA; !d; COMMA; !e; PLUS; #2; TIMES; EXPRESSION ROUND(!a; POWER; !b))))))",
                "!a", SQUARE_BRACKET_START_ID, "!b", COMMA_ID, "!d", COMMA_ID, "!e",
                PLUS_ID, "#2", TIMES_ID, ROUND_BRACKET_START_ID, "!a", POWER_ID, "!b", ROUND_BRACKET_END_ID, SQUARE_BRACKET_END_ID);

        assertResults(null,"PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; EXPRESSION SQUARE(!b)))))",
                "!a", SQUARE_BRACKET_START_ID, "!b", COMMA_ID, SQUARE_BRACKET_END_ID);

        assertResults(null,"PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; ASSIGN; EXPRESSION SQUARE(#3; COMMA; #4; COMMA; #5)))))",
                "!a", ASSIGN_ID, SQUARE_BRACKET_START_ID, "#3", COMMA_ID, "#4", COMMA_ID, "#5",COMMA_ID, SQUARE_BRACKET_END_ID);

        assertResults(null,"PROGRAM(METHOD main(TYPE NOTHING; CODE_BLOCK(EXPRESSION(!a; ASSIGN; !b; EXPRESSION SQUARE(!c; COMMA; !d; EXPRESSION SQUARE(!e)); PLUS; !f))))",
                "!a", ASSIGN_ID, "!b", SQUARE_BRACKET_START_ID, "!c", COMMA_ID, "!d",
                SQUARE_BRACKET_START_ID, "!e", SQUARE_BRACKET_END_ID, SQUARE_BRACKET_END_ID, PLUS_ID, "!f");
    }
}
