package in.costea;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constants{
    private Constants(){}

    static final Map<String,String> keywords=new LinkedHashMap<>();
    static
    {
           keywords.put("true","TRUE");
           keywords.put("false","FALSE");
           keywords.put("null","NULL");
           keywords.put("byte","INT8");
           keywords.put("shortint","INT16");
           keywords.put("int","INT32");
           keywords.put("longint","INT64");
           keywords.put("infint","BIGINT");
           keywords.put("bit","BOOLEAN");
           keywords.put("text","STRING");
           keywords.put("decimal","DOUBLE");
           keywords.put("exactdec","DECIMAL");
           keywords.put("list","ARRAYLIST");
           keywords.put("optional","NULLABLE");
           keywords.put("dict","MAP");
           keywords.put("range","RANGE");
           keywords.put("set","SET");
           keywords.put("method","DECLARE_METHOD");
           keywords.put("var","DECLARE_VARIABLE");
           keywords.put("let","DECLARE_CONSTANT");
           keywords.put("if","IF");
           keywords.put("then","THEN");
           keywords.put("otherwise","ELSE");
           keywords.put("for","FOR");
           keywords.put("in","IN");
           keywords.put("do","DO");
           keywords.put("while","WHILE");
           keywords.put("mod","MOD");
           keywords.put("and","AND");
           keywords.put("or","OR");
           keywords.put("not","NOT");
           keywords.put("from","RANGE_FROM");
           keywords.put("to","RANGE_TO");
           keywords.put("by","RANGE_BY");
           keywords.put("break","BREAK");
           keywords.put("continue","CONTINUE");
           keywords.put("return","RETURN");
           keywords.put("begin","START_BLOCK");
           keywords.put("end","END_BLOCK");
    }

    static final Map<String,String> operators=new LinkedHashMap<>();
    static
    {
           operators.put("+","PLUS");
           operators.put("-","MINUS");
           operators.put("*","TIMES");
           operators.put("/","DIVIDE");
           operators.put("^","POWER");
           operators.put("=","EQUALITY");
           operators.put(">","LARGER");
           operators.put("<","SMALLER");
           operators.put("(","ROUND_BRACKET_START");
           operators.put(")","ROUND_BRACKET_END");
           operators.put("[","SQUARE_BRACKET_START");
           operators.put("]","SQUARE_BRACKET_END");
           operators.put(",","COMMA");
           operators.put(".","DOT");
           operators.put(":","COLON");
           operators.put(";","END_STATEMENT");
           operators.put(" ","SPACE");
           operators.put("\n","NEWLINE");
           operators.put(":=","ASSIGN");
           operators.put(">=","LARGER_EQUALS");
           operators.put("<=","SMALLER_EQUALS");
           operators.put("=/=","NOT_EQUAL");
           operators.put("+=","ASSIGN_PLUS");
           operators.put("-=","ASSIGN_MINUS");
           operators.put("*=","ASSIGN_TIMES");
           operators.put("/=","ASSIGN_DIVIDE");
           operators.put("^=","ASSIGN_POWER");
           //operators.put("$=","TEMP");
    }
}
