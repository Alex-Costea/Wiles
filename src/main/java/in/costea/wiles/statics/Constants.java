package in.costea.wiles.statics;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.max;

public class Constants{
    private Constants()
    {

    }
    public static final boolean DEBUG=true;

    public static final String UNKNOWN_TOKEN="ERROR_TOKEN";
    public static final String START_BLOCK_ID ="START_BLOCK";
    public static final String END_BLOCK_ID ="END_BLOCK";
    public static final String SPACE_ID="SPACE";
    public static final String NEWLINE_ID="NEWLINE";
    public static final String ROUND_BRACKET_START_ID="ROUND_BRACKET_START";
    public static final String ROUND_BRACKET_END_ID="ROUND_BRACKET_END";
    public static final String METHOD_DECLARATION_ID="DECLARE_METHOD";

    public static final Map<String,String> KEYWORDS =new LinkedHashMap<>();
    static
    {
           KEYWORDS.put("true","TRUE");
           KEYWORDS.put("false","FALSE");
           KEYWORDS.put("nothing","NULL");
           KEYWORDS.put("bit","BOOLEAN"); //1 bit
           KEYWORDS.put("byte","INT8"); //8 bits
           KEYWORDS.put("smallint","INT16"); //16 bits
           KEYWORDS.put("int","INT32"); //32 bits
           KEYWORDS.put("bigint","INT64"); //64 bits
           KEYWORDS.put("infint","BIG_NUM"); //unlimited bits
           KEYWORDS.put("text","STRING");
           KEYWORDS.put("decimal","DOUBLE");
           KEYWORDS.put("exactdec","DECIMAL");
           KEYWORDS.put("list","ARRAY_LIST");
           KEYWORDS.put("linkedlist","LINKED_LIST");
           KEYWORDS.put("optional","NULLABLE");
           KEYWORDS.put("dictionary","LINKED_MAP");
           KEYWORDS.put("range","RANGE");
           KEYWORDS.put("set","SET");
           KEYWORDS.put("method",METHOD_DECLARATION_ID);
           KEYWORDS.put("var","DECLARE_VARIABLE");
           KEYWORDS.put("let","DECLARE_CONSTANT");
           KEYWORDS.put("if","IF");
           KEYWORDS.put("then","THEN");
           KEYWORDS.put("otherwise","ELSE");
           KEYWORDS.put("for","FOR");
           KEYWORDS.put("in","IN");
           KEYWORDS.put("do","DO");
           KEYWORDS.put("while","WHILE");
           KEYWORDS.put("mod","MOD");
           KEYWORDS.put("and","AND");
           KEYWORDS.put("or","OR");
           KEYWORDS.put("xor","XOR");
           KEYWORDS.put("not","NOT");
           KEYWORDS.put("from","RANGE_FROM");
           KEYWORDS.put("to","RANGE_TO");
           KEYWORDS.put("by","RANGE_BY");
           KEYWORDS.put("stop","BREAK");
           KEYWORDS.put("skip","CONTINUE");
           KEYWORDS.put("result","RETURN");
           KEYWORDS.put("begin", START_BLOCK_ID);
           KEYWORDS.put("end", END_BLOCK_ID);
    }

    public enum SYNTAX_TYPE {
        PROGRAM,
        METHOD,
        IDENTIFIER,
    }

    public static final Map<String,String> OPERATORS =new LinkedHashMap<>();
    static
    {
           OPERATORS.put("+","PLUS");
           OPERATORS.put("-","MINUS");
           OPERATORS.put("*","TIMES");
           OPERATORS.put("/","DIVIDE");
           OPERATORS.put("^","POWER");
           OPERATORS.put("=","EQUALS");
           OPERATORS.put(">","LARGER");
           OPERATORS.put("<","SMALLER");
           OPERATORS.put("(",ROUND_BRACKET_START_ID);
           OPERATORS.put(")",ROUND_BRACKET_END_ID);
           OPERATORS.put("[","SQUARE_BRACKET_START");
           OPERATORS.put("]","SQUARE_BRACKET_END");
           OPERATORS.put(",","COMMA");
           OPERATORS.put(".","DOT");
           OPERATORS.put(":","COLON");
           OPERATORS.put(";","END_STATEMENT");
           OPERATORS.put(" ",SPACE_ID);
           OPERATORS.put("\n",NEWLINE_ID);
           OPERATORS.put(":=","ASSIGN");
           OPERATORS.put(">=","LARGER_EQUALS");
           OPERATORS.put("<=","SMALLER_EQUALS");
           OPERATORS.put("=/=","NOT_EQUAL");
           OPERATORS.put("+=","ASSIGN_PLUS");
           OPERATORS.put("-=","ASSIGN_MINUS");
           OPERATORS.put("*=","ASSIGN_TIMES");
           OPERATORS.put("/=","ASSIGN_DIVIDE");
           OPERATORS.put("^=","ASSIGN_POWER");
           OPERATORS.put("\\","CONTINUE_LINE");
           if(DEBUG)
           {
               OPERATORS.put("$=", "TEMP");
               OPERATORS.put("=$=", "TEMP2");
           }
    }

    public static final int MAX_OPERATOR_LENGTH = 5;
    static
    {
        if(max(OPERATORS.keySet().stream().mapToInt(String::length).boxed().toList())>MAX_OPERATOR_LENGTH)
            throw new IllegalArgumentException("MAX_OPERATOR_LENGTH smaller than length of largest operator!");
    }
    public static final String IDENTIFIER_START="!";
    public static final String STRING_START="@";
    public static final String NUM_START="#";
    public static final char STRING_DELIMITER='"';
    public static final char DECIMAL_DELIMITER ='.';
    public static final char COMMENT_START ='#';
    public static final char COMMENT_END='\n';
}
