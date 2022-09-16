import in.costea.wiles.commands.SyntaxTree;
import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import org.junit.jupiter.api.Test;
import in.costea.wiles.data.Token;
import static in.costea.wiles.statics.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class CreateConverter
{
    TokensToSyntaxTreeConverter converter;
    CompilationExceptionsCollection exceptions;
    SyntaxTree tree;
    public CreateConverter(String... tokens)
    {
        converter = new TokensToSyntaxTreeConverter(Arrays.stream(tokens).map(Token::new).toList());
        tree =converter.convert();
        exceptions=converter.getExceptions();
    }
    public CompilationExceptionsCollection getExceptions() {
        return exceptions;
    }

    public String getResult() {
        return tree.toString();
    }
}

public class SyntaxTreeConverterTests {
    @Test
    void emptyBodyTest()
    {
        //Not yet implemented
        assertThrows(Error.class, CreateConverter::new);
        assertThrows(Error.class,()->new CreateConverter(NEWLINE_ID));
    }

    public void assertResults(CompilationExceptionsCollection exceptions, String expectedResult, String... tokens)
    {
        CreateConverter converter=new CreateConverter(tokens);
        if(exceptions==null)
            exceptions=new CompilationExceptionsCollection();
        assertEquals(exceptions,converter.getExceptions());
        if(expectedResult!=null)
            assertEquals(expectedResult, converter.getResult());
    }

    @Test
    public void methodDeclarationTest()
    {
        assertResults(null, "PROGRAM(METHOD a (METHOD_BODY))",
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a (METHOD_BODY))",
                NEWLINE_ID,NEWLINE_ID, DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a (METHOD_BODY))",
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,
                NEWLINE_ID,ROUND_BRACKET_END_ID,
                NEWLINE_ID,START_BLOCK_ID,
                NEWLINE_ID,END_BLOCK_ID);

        assertResults(null, "PROGRAM(METHOD a (METHOD_BODY(IDENTIFIER !b ; IDENTIFIER !c )))",
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,"!b","!c",END_BLOCK_ID);
    }

    private CompilationExceptionsCollection createExceptions(CompilationException... list)
    {
        CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
        exceptions.add(list);
        return exceptions;
    }

    @Test
    public void programExceptionsTest()
    {
        assertResults(createExceptions(new TokenExpectedException("Expected line end!",null)),
                null,
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,END_BLOCK_ID,"!b");

        assertResults(createExceptions(new TokenExpectedException("Token \"begin\" expected!",null),
                        new TokenExpectedException("Token \"method\" expected!",null)),
                null,
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID,
                END_BLOCK_ID,NEWLINE_ID,"!c","!d",NEWLINE_ID,"!e");

        assertResults(createExceptions(new UnexpectedEndException("Missing token: \"end\"")),
                null,
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID,START_BLOCK_ID);

        assertResults(createExceptions(new UnexpectedEndException("Missing token: \")\"")),
                null,
                DECLARE_METHOD_ID,"!a",ROUND_BRACKET_START_ID);

        assertResults(createExceptions(new UnexpectedEndException("Expected method name!")),
                null,
                DECLARE_METHOD_ID);

        assertResults(createExceptions(new UnexpectedEndException("Token \"begin\" expected!"),
                        new UnexpectedEndException("Token \"begin\" expected!")),
                null,
                DECLARE_METHOD_ID,"!name",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID, FINISH_STATEMENT,
                DECLARE_METHOD_ID,"!name",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID, FINISH_STATEMENT);
    }
}
