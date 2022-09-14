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
        var x = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID,START_BLOCK_ID,END_BLOCK_ID);
        assertEquals(new CompilationExceptionsCollection(),x.getExceptions());

        x = createConverter(
                METHOD_DECLARATION_ID,"!a",ROUND_BRACKET_START_ID,ROUND_BRACKET_END_ID,START_BLOCK_ID,END_BLOCK_ID,"!b");
        var exceptions=new CompilationExceptionsCollection();
        exceptions.add(new TokenExpectedException("Method declaration expected!",null));
        assertEquals(exceptions,x.getExceptions());
    }
}
