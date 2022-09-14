import in.costea.wiles.converters.TokensToSyntaxTreeConverter;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.TokenExpectedException;
import org.junit.jupiter.api.Test;
import in.costea.wiles.data.Token;
import static in.costea.wiles.statics.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class SyntaxTreeConverterTests {
    public TokensToSyntaxTreeConverter createConverter(String... tokens)
    {
        var temp = new TokensToSyntaxTreeConverter(Arrays.stream(tokens).map(Token::new).toList());
        temp.convert();
        return temp;
    }
    @Test
    public void SyntaxTreeConverterTest()
    {
        TokensToSyntaxTreeConverter converter;
        CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();

        converter = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,END_BLOCK_ID);
        assertEquals(exceptions,converter.getExceptions());

        converter = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,
                NEWLINE_ID,ROUND_BRACKET_END_ID,
                NEWLINE_ID,START_BLOCK_ID,
                NEWLINE_ID,END_BLOCK_ID);
        assertEquals(exceptions,converter.getExceptions());

        converter = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID
                ,START_BLOCK_ID,END_BLOCK_ID,"!b");
        exceptions=new CompilationExceptionsCollection();
        exceptions.add(new TokenExpectedException("Method declaration expected!",null));
        assertEquals(exceptions,converter.getExceptions());

        converter = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID,
                END_BLOCK_ID,"!b","!c");
        exceptions=new CompilationExceptionsCollection();
        exceptions.add(new TokenExpectedException("Start block expected!",null),
                new TokenExpectedException("Method declaration expected!",null));
        assertEquals(exceptions,converter.getExceptions());
    }
}
