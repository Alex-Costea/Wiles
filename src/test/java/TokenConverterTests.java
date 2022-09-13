import in.costea.wiles.converters.InputToTokensConverter;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.StringUnfinishedException;
import in.costea.wiles.exceptions.UnknownOperatorException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.DEBUG;
import static in.costea.wiles.statics.Constants.MAX_OPERATOR_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.*;

public class TokenConverterTests {
    public void TokenConverterEquals(String input, String[] solution)
    {
        List<Token> solutionList=new ArrayList<>();
        for(String s:solution)
        {
            solutionList.add(new Token(s));
        }
        assertEquals(new InputToTokensConverter(input).convert(), solutionList);
    }

    public void TokenConverterThrows(Integer exceptionIndex,String input, Class<? extends Throwable> throwing, String message, Integer line)
    {
        var x=new InputToTokensConverter(input);
        x.convert();
        Throwable t;
        if(message!=null) t = assertThrows(throwing, ()->x.throwExceptionIfExists(exceptionIndex),message);
        else t = assertThrows(throwing, ()->x.throwExceptionIfExists(exceptionIndex));
        assert t instanceof CompilationException;
        if(line!=null)
            assertEquals(line, ((CompilationException) t).getLine());
    }

    public void TokenConverterThrows(Integer exceptionIndex,String input, Class<? extends Throwable> throwing, Integer line)
    {
        TokenConverterThrows(exceptionIndex, input, throwing, null,line);
    }

    public void TokenConverterThrows(Integer exceptionIndex,String input, Class<? extends Throwable> throwing, String message)
    {
        TokenConverterThrows(exceptionIndex, input, throwing, message,null);
    }

    public void TokenConverterThrows(Integer exceptionIndex,String input, Class<? extends Throwable> throwing)
    {
        TokenConverterThrows(exceptionIndex, input, throwing, null,null);
    }

    @Test
    public void EmptyInputsTest()
    {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class,() -> new InputToTokensConverter(null));
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
    @SuppressWarnings("ConstantConditions")
    public void OperatorsTest()
    {
        TokenConverterEquals("=/=",new String[]{"NOT_EQUAL"});
        TokenConverterEquals("=/=/=/",new String[]{"NOT_EQUAL","ASSIGN_DIVIDE","DIVIDE"});
        TokenConverterEquals("=/=/=/",new String[]{"NOT_EQUAL","ASSIGN_DIVIDE","DIVIDE"});
        TokenConverterThrows(0,"$", UnknownOperatorException.class);
        TokenConverterThrows(0,"=$", UnknownOperatorException.class, "Operator unknown: $");

        String invalidProgram="${}{}{}{}{}";

        assumingThat(invalidProgram.length() >= (MAX_OPERATOR_LENGTH + 1),()->{
            String substring1=invalidProgram.substring(1, MAX_OPERATOR_LENGTH+1);
            TokenConverterThrows(0,invalidProgram, UnknownOperatorException.class, "Operator unknown: "+substring1);

            assumingThat(invalidProgram.length()>=2*MAX_OPERATOR_LENGTH+1,()->{
                String substring2=invalidProgram.substring(MAX_OPERATOR_LENGTH+1, 2*MAX_OPERATOR_LENGTH+1);
                TokenConverterThrows(1,invalidProgram, UnknownOperatorException.class, "Operator unknown: "+substring2);
            });

        });


        if(DEBUG){
            TokenConverterEquals("$=", new String[]{"TEMP"});
            TokenConverterEquals("=$=", new String[]{"TEMP2"});
        }

        TokenConverterThrows(0,"$\n@", UnknownOperatorException.class, "Operator unknown: $");
        TokenConverterThrows(1,"$\n@", UnknownOperatorException.class, "Operator unknown: @");
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
        TokenConverterThrows(0,"\"abc", StringUnfinishedException.class,"String unfinished: abc");
        TokenConverterEquals("\"\"\"\"",new String[]{"@","@"});
        TokenConverterThrows(0,"\"\"\"\"\"", StringUnfinishedException.class,"String unfinished: ");
        TokenConverterThrows(0,"abc\"def\nghi\"jkl", StringUnfinishedException.class);
        TokenConverterThrows(0,"true\n\nhello\"\n\"", StringUnfinishedException.class,3);
        TokenConverterThrows(1,"@\n\"\n\"\n", StringUnfinishedException.class,2);
        TokenConverterThrows(2,"@\n\"\n\"\n", StringUnfinishedException.class,3);
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
