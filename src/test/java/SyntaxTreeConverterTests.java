import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static in.costea.wiles.statics.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateConverter
{
    TokensToSyntaxTreeConverter converter;
    CompilationExceptionsCollection exceptions;
    SyntaxTree tree;

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

public class SyntaxTreeConverterTests
{

    public void assertResults(CompilationExceptionsCollection exceptions, String expectedResult, String... tokens)
    {
        CreateConverter converter = new CreateConverter(tokens);
        if (exceptions == null)
            exceptions = new CompilationExceptionsCollection();
        assertEquals(exceptions, converter.getExceptions());
        if (expectedResult != null)
            assertEquals(expectedResult, converter.getResult());
    }

    @Test
    public void newlineTests()
    {
        assertResults(null, "PROGRAM(METHOD a (TYPE NOTHING ; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a (TYPE NOTHING ; CODE_BLOCK))",
                NEWLINE_ID, NEWLINE_ID, DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a (TYPE NOTHING ; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!a", ROUND_BRACKET_START_ID,
                NEWLINE_ID, ROUND_BRACKET_END_ID,
                NEWLINE_ID, START_BLOCK_ID,
                NEWLINE_ID, END_BLOCK_ID);
    }

    @Test
    public void operationsTest()
    {
        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!b; ASSIGN; !c))))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, "!b", ASSIGN_ID, "!c", END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!b; ASSIGN; #3))))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID
                , START_BLOCK_ID, "!b", ASSIGN_ID, "#3", END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!b; PLUS; #3; MINUS; #5))))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5");

        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!a; PLUS; !b); OPERATION(#0; PLUS; !c); OPERATION(!a; PLUS; !b; PLUS; !c))))",
                "!a", PLUS_ID, "!b", NEWLINE_ID, PLUS_ID, "!c", NEWLINE_ID, NEWLINE_ID,
                "!a", PLUS_ID, NEWLINE_ID, "!b", PLUS_ID, "!c");

        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!b; PLUS; #3; MINUS; #5))))",
                "!b", PLUS_ID, "#3", MINUS_ID, "#5");

        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!c; ASSIGN; #0; MINUS; #10; PLUS; #0; PLUS; OPERATION(#0; PLUS; #10)))))",
                "!c", ASSIGN_ID, MINUS_ID, "#10", PLUS_ID, NEWLINE_ID, PLUS_ID, ROUND_BRACKET_START_ID, PLUS_ID, "#10", ROUND_BRACKET_END_ID);

    }

    @Test
    public void operationsTestException()
    {
        assertResults(createExceptions(new UnexpectedEndException("Operation unfinished!", null)),
                null,
                "!a", PLUS_ID, "!b", PLUS_ID);

        assertResults(createExceptions(new TokenExpectedException("Identifier or unary operator expected!", null)),
                null,
                "!b", PLUS_ID, TIMES_ID, "#5");

        assertResults(createExceptions(new UnexpectedTokenException("*", null)),
                null,
                TIMES_ID, "!a");
    }

    @Test
    public void parenthesesTests()
    {
        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!a; PLUS; OPERATION(OPERATION(!b; PLUS; !c); PLUS; !d)))))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c",
                ROUND_BRACKET_END_ID, PLUS_ID, "!d", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!a; PLUS; OPERATION(!b; PLUS; !c)))))",
                "!a", PLUS_ID, ROUND_BRACKET_START_ID, NEWLINE_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!a; PLUS; OPERATION(!b; PLUS; !c)))))",
                "!a", PLUS_ID, NEWLINE_ID, ROUND_BRACKET_START_ID, "!b", PLUS_ID, "!c", ROUND_BRACKET_END_ID);
        assertResults(null, "PROGRAM(METHOD main (TYPE NOTHING ; CODE_BLOCK(OPERATION(!a; PLUS; !b))))",
                ROUND_BRACKET_START_ID, "!a", PLUS_ID, "!b", ROUND_BRACKET_END_ID);
    }

    private CompilationExceptionsCollection createExceptions(CompilationException... list)
    {
        CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
        exceptions.add(list);
        return exceptions;
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
                DECLARE_METHOD_ID, "!name", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, END_BLOCK_ID, FINISH_STATEMENT_ID,
                DECLARE_METHOD_ID, "!name", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, FINISH_STATEMENT_ID);

        assertResults(createExceptions(new UnexpectedTokenException("Cannot declare method in body-only mode!", null)),
                null,
                "!a", PLUS_ID, "!b", NEWLINE_ID,
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, START_BLOCK_ID, END_BLOCK_ID);
    }

    @Test
    public void methodTest()
    {
        assertResults(null, "PROGRAM(METHOD main (TYPE INT32 ; CODE_BLOCK))",
                DECLARE_METHOD_ID, "!main", ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID, COLON_ID, "INT32",
                START_BLOCK_ID, END_BLOCK_ID);
    }
}
