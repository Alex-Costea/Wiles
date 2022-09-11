package in.costea.wiles;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constants{
    private Constants(){}

    static final Map<String,String> KEYWORDS =new LinkedHashMap<>();
    static
    {
           KEYWORDS.put("true","TRUE");
           KEYWORDS.put("false","FALSE");
           KEYWORDS.put("null","NULL");
           KEYWORDS.put("byte","INT8");
           KEYWORDS.put("shortint","INT16");
           KEYWORDS.put("int","INT32");
           KEYWORDS.put("longint","INT64");
           KEYWORDS.put("infint","BIGINT");
           KEYWORDS.put("bit","BOOLEAN");
           KEYWORDS.put("text","STRING");
           KEYWORDS.put("decimal","DOUBLE");
           KEYWORDS.put("exactdec","DECIMAL");
           KEYWORDS.put("list","ARRAYLIST");
           KEYWORDS.put("optional","NULLABLE");
           KEYWORDS.put("dict","MAP");
           KEYWORDS.put("range","RANGE");
           KEYWORDS.put("set","SET");
           KEYWORDS.put("method","DECLARE_METHOD");
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
           KEYWORDS.put("not","NOT");
           KEYWORDS.put("from","RANGE_FROM");
           KEYWORDS.put("to","RANGE_TO");
           KEYWORDS.put("by","RANGE_BY");
           KEYWORDS.put("break","BREAK");
           KEYWORDS.put("continue","CONTINUE");
           KEYWORDS.put("return","RETURN");
           KEYWORDS.put("begin","START_BLOCK");
           KEYWORDS.put("end","END_BLOCK");
    }

    static final Map<String,String> OPERATORS =new LinkedHashMap<>();
    static
    {
           OPERATORS.put("+","PLUS");
           OPERATORS.put("-","MINUS");
           OPERATORS.put("*","TIMES");
           OPERATORS.put("/","DIVIDE");
           OPERATORS.put("^","POWER");
           OPERATORS.put("=","EQUALITY");
           OPERATORS.put(">","LARGER");
           OPERATORS.put("<","SMALLER");
           OPERATORS.put("(","ROUND_BRACKET_START");
           OPERATORS.put(")","ROUND_BRACKET_END");
           OPERATORS.put("[","SQUARE_BRACKET_START");
           OPERATORS.put("]","SQUARE_BRACKET_END");
           OPERATORS.put(",","COMMA");
           OPERATORS.put(".","DOT");
           OPERATORS.put(":","COLON");
           OPERATORS.put(";","END_STATEMENT");
           OPERATORS.put(" ","SPACE");
           OPERATORS.put("\n","NEWLINE");
           OPERATORS.put(":=","ASSIGN");
           OPERATORS.put(">=","LARGER_EQUALS");
           OPERATORS.put("<=","SMALLER_EQUALS");
           OPERATORS.put("=/=","NOT_EQUAL");
           OPERATORS.put("+=","ASSIGN_PLUS");
           OPERATORS.put("-=","ASSIGN_MINUS");
           OPERATORS.put("*=","ASSIGN_TIMES");
           OPERATORS.put("/=","ASSIGN_DIVIDE");
           OPERATORS.put("^=","ASSIGN_POWER");
           //operators.put("$=","TEMP");
    }
}
