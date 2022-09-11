package in.costea;

import java.util.HashMap;
import java.util.Map;

public class Constants{
    private Constants(){}

    static final Map<String,String> keywords=new HashMap<>();
    static
    {
           keywords.put("TRUE","true");
           keywords.put("FALSE","false");
           keywords.put("NULL","null");
           keywords.put("INT8","byte");
           keywords.put("INT16","shortint");
           keywords.put("INT32","int");
           keywords.put("INT64","longint");
           keywords.put("BIGINT","infint");
           keywords.put("BOOLEAN","bit");
           keywords.put("STRING","text");
           keywords.put("DOUBLE","decimal");
           keywords.put("DECIMAL","exactdec");
           keywords.put("ARRAYLIST","list");
           keywords.put("NULLABLE","optional");
           keywords.put("MAP","dict");
           keywords.put("RANGE","range");
           keywords.put("SET","set");
           keywords.put("DECLARE_METHOD","method");
           keywords.put("DECLARE_VARIABLE","var");
           keywords.put("DECLARE_CONSTANT","let");
           keywords.put("IF","if");
           keywords.put("THEN","then");
           keywords.put("ELSE","otherwise");
           keywords.put("FOR","for");
           keywords.put("IN","in");
           keywords.put("DO","do");
           keywords.put("WHILE","while");
           keywords.put("MOD","mod");
           keywords.put("AND","and");
           keywords.put("OR","or");
           keywords.put("NOT","not");
           keywords.put("RANGE_FROM","from");
           keywords.put("RANGE_TO","to");
           keywords.put("RANGE_BY","by");
           keywords.put("BREAK","break");
           keywords.put("CONTINUE","continue");
           keywords.put("RETURN","return");
           keywords.put("START_BLOCK","begin");
           keywords.put("END_BLOCK","end");
    }

    static final Map<String,String> operators=new HashMap<>();
    static
    {
           operators.put("PLUS","+");
           operators.put("MINUS","-");
           operators.put("TIMES","*");
           operators.put("DIVIDE","/");
           operators.put("POWER","^");
           operators.put("EQUALITY","=");
           operators.put("LARGER",">");
           operators.put("LARGER_EQUALS",">=");
           operators.put("SMALLER","<");
           operators.put("SMALLER_EQUALS","<=");
           operators.put("NOT_EQUAL","=/=");
           operators.put("ASSIGN",":=");
           operators.put("ASSIGN_PLUS","+=");
           operators.put("ASSIGN_MINUS","-=");
           operators.put("ASSIGN_TIMES","*=");
           operators.put("ASSIGN_DIVIDE","/=");
           operators.put("ASSIGN_POWER","^=");
           operators.put("ROUND_BRACKET_START","(");
           operators.put("ROUND_BRACKET_END",")");
           operators.put("SQUARE_BRACKET_START","[");
           operators.put("SQUARE_BRACKET_END","]");
           operators.put("COMMA",",");
           operators.put("DOT",".");
           operators.put("TYPE",":");
           operators.put("END_STATEMENT",";");
           operators.put("NEWLINE","\n");
           operators.put("SPACE"," ");
    }
}
