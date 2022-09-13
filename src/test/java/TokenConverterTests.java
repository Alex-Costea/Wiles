import in.costea.wiles.TokensConverter;
import in.costea.wiles.exceptions.StringUnfinishedException;
import in.costea.wiles.exceptions.UnknownOperatorException;
import org.junit.jupiter.api.*;

import java.util.List;

import static in.costea.wiles.statics.Constants.DEBUG;
import static in.costea.wiles.statics.Constants.MAX_OPERATOR_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenConverterTests {
    public void TokenConverterEquals(String input, String[] solution)
    {
        List<String> solutionList=List.of(solution);
        assertEquals(new TokensConverter(input).convert(), solutionList);
    }

    public void TokenConverterThrows(String input, Class<? extends Throwable> throwing,String message)
    {
        var x=new TokensConverter(input);
        x.convert();
        assertThrows(throwing, x::throwFirstExceptionIfExists,message);
    }

    public void TokenConverterThrows(String input, Class<? extends Throwable> throwing)
    {
        var x=new TokensConverter(input);
        x.convert();
        assertThrows(throwing, x::throwFirstExceptionIfExists);
    }


    @Test
    public void EmptyInputsTest()
    {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class,() -> new TokensConverter(null));
        TokenConverterEquals("",new String[]{});
        TokenConverterEquals("     ",new String[]{});
    }

    @Test
    public void CommentTest()
    {
        TokenConverterEquals("#",new String[]{});
        TokenConverterEquals("#\n",new String[]{"NEWLINE"});
        TokenConverterEquals("abc#de\nfgh",new String[]{"!abc","NEWLINE","!fgh"});
        TokenConverterEquals("abc#a b c d e f break end continue",new String[]{"!abc"});
    }

    @Test
    public void OperatorsTest()
    {
        TokenConverterEquals("=/=",new String[]{"NOT_EQUAL"});
        TokenConverterEquals("=/=/=/",new String[]{"NOT_EQUAL","ASSIGN_DIVIDE","DIVIDE"});
        TokenConverterEquals("=/=/=/",new String[]{"NOT_EQUAL","ASSIGN_DIVIDE","DIVIDE"});
        TokenConverterThrows("$", UnknownOperatorException.class);
        TokenConverterThrows("=$", UnknownOperatorException.class, "Operator unknown: $");

        String invalidProgram="*$%^&*%{&}";
        TokenConverterThrows(invalidProgram, UnknownOperatorException.class,
                "Operator unknown: "+invalidProgram.substring(1, MAX_OPERATOR_LENGTH+1));

        if(DEBUG){
            TokenConverterEquals("$=", new String[]{"TEMP"});
            TokenConverterEquals("=$=", new String[]{"TEMP2"});
        }
    }

    @Test
    public void NumericalLiteralsTest()
    {
        TokenConverterEquals("1",new String[]{"#1"});
        TokenConverterEquals(".1",new String[]{"DOT","#1"});
        TokenConverterEquals("1.",new String[]{"#1","DOT"});
        TokenConverterEquals("1.length",new String[]{"#1","DOT","!length"});
        TokenConverterEquals("1.2",new String[]{"#1.2"});
        TokenConverterEquals("1.2.3.4.5",new String[]{"#1.2","DOT","#3.4","DOT","#5"});
    }

    @Test
    public void StringLiteralsTest()
    {
        TokenConverterEquals("\"abc\"",new String[]{"@abc"});
        TokenConverterThrows("\"abc", StringUnfinishedException.class,"String unfinished: abc");
        TokenConverterEquals("\"\"\"\"",new String[]{"@","@"});
        TokenConverterThrows("\"\"\"\"\"", StringUnfinishedException.class,"String unfinished: ");
    }

    @Test
    public void IdentifiersTest()
    {
        TokenConverterEquals("a b c",new String[]{"!a","!b","!c"});
        TokenConverterEquals("__xXx__",new String[]{"!__xXx__"});
        TokenConverterEquals("a12",new String[]{"!a12"});
        TokenConverterEquals("2ab",new String[]{"#2","!ab"});
        TokenConverterEquals("français",new String[]{"!français"});
        TokenConverterEquals("日本語",new String[]{"!日本語"});
        TokenConverterEquals("i do not stop the end",new String[]{"!i","DO","NOT","BREAK","!the","END_BLOCK"});
    }

}
