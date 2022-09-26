package in.costea.wiles.statics;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Set;

import static java.util.Collections.max;

public class Constants
{
    public static final boolean DEBUG = true;
    public static final String UNKNOWN_TOKEN = "ERROR_TOKEN";
    public static final String START_BLOCK_ID = "START_BLOCK";
    public static final String END_BLOCK_ID = "END_BLOCK";
    public static final String SPACE_ID = "SPACE";
    public static final String NEWLINE_ID = "NEWLINE";
    public static final String ROUND_BRACKET_START_ID = "ROUND_BRACKET_START";
    public static final String ROUND_BRACKET_END_ID = "ROUND_BRACKET_END";
    public static final String DECLARE_METHOD_ID = "DECLARE_METHOD";
    public static final String FINISH_STATEMENT_ID = "FINISH_STATEMENT";
    public static final String CONTINUE_LINE_ID = "CONTINUE_LINE";
    public static final String PLUS_ID = "PLUS";
    public static final String MINUS_ID = "MINUS";
    public static final String TIMES_ID = "TIMES";
    public static final String DIVIDE_ID = "DIVIDE";
    public static final String POWER_ID = "POWER";
    public static final String ASSIGN_ID = "ASSIGN";
    public static final String IDENTIFIER_START = "!";
    public static final String STRING_START = "@";
    public static final String NUM_START = "#";
    public static final String MAIN_METHOD_NAME = "main";
    public static final BiMap<String, String> KEYWORDS = HashBiMap.create();
    public static final BiMap<String, String> OPERATORS = HashBiMap.create();
    public static final BiMap<String, String> TOKENS;
    public static final BiMap<String, String> TOKENS_INVERSE;
    public static final int MAX_OPERATOR_LENGTH = 3;
    public static final char STRING_DELIMITER = '"';
    public static final char DECIMAL_DELIMITER = '.';
    public static final char DIGIT_SEPARATOR = '_';
    public static final char COMMENT_START = '#';
    public static final char NEWLINE = '\n';
    public static final char SPACE = ' ';
    public static final char CONTINUE_LINE = '\\';
    public static final Set<String> ALLOWED_OPERATORS_IN_OPERATION = Set.of(PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID, ASSIGN_ID);
    public static final Set<String> UNARY_OPERATORS = Set.of(PLUS_ID, MINUS_ID);
    public static final Set<String> ROUND_BRACKETS = Set.of(ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID);
    public static final Set<String> STATEMENT_ENDERS = Set.of(NEWLINE_ID, FINISH_STATEMENT_ID);

    static
    {
        KEYWORDS.put("true", "TRUE");
        KEYWORDS.put("false", "FALSE");
        KEYWORDS.put("nothing", "NULL");
        KEYWORDS.put("bit", "BOOLEAN"); //1 bit
        KEYWORDS.put("byte", "INT8"); //8 bits
        KEYWORDS.put("smallint", "INT16"); //16 bits
        KEYWORDS.put("int", "INT32"); //32 bits
        KEYWORDS.put("bigint", "INT64"); //64 bits
        KEYWORDS.put("text", "STRING");
        KEYWORDS.put("decimal", "DOUBLE");
        KEYWORDS.put("list", "ARRAY_LIST");
        KEYWORDS.put("optional", "NULLABLE");
        KEYWORDS.put("dictionary", "LINKED_MAP");
        KEYWORDS.put("range", "RANGE");
        KEYWORDS.put("method", DECLARE_METHOD_ID);
        KEYWORDS.put("var", "DECLARE_VARIABLE");
        KEYWORDS.put("val", "DECLARE_CONSTANT");
        KEYWORDS.put("if", "IF");
        KEYWORDS.put("then", "THEN");
        KEYWORDS.put("otherwise", "ELSE");
        KEYWORDS.put("for", "FOR");
        KEYWORDS.put("in", "IN");
        KEYWORDS.put("do", "DO");
        KEYWORDS.put("while", "WHILE");
        KEYWORDS.put("mod", "MOD");
        KEYWORDS.put("and", "AND");
        KEYWORDS.put("or", "OR");
        KEYWORDS.put("xor", "XOR");
        KEYWORDS.put("not", "NOT");
        KEYWORDS.put("from", "RANGE_FROM");
        KEYWORDS.put("to", "RANGE_TO");
        KEYWORDS.put("by", "RANGE_BY");
        KEYWORDS.put("stop", "BREAK");
        KEYWORDS.put("skip", "CONTINUE");
        KEYWORDS.put("result", "RETURN");
        KEYWORDS.put("begin", START_BLOCK_ID);
        KEYWORDS.put("end", END_BLOCK_ID);
    }

    static
    {
        OPERATORS.put("+", PLUS_ID);
        OPERATORS.put("-", MINUS_ID);
        OPERATORS.put("*", TIMES_ID);
        OPERATORS.put("/", DIVIDE_ID);
        OPERATORS.put("^", POWER_ID);
        OPERATORS.put("=", "EQUALS");
        OPERATORS.put(">", "LARGER");
        OPERATORS.put("<", "SMALLER");
        OPERATORS.put("(", ROUND_BRACKET_START_ID);
        OPERATORS.put(")", ROUND_BRACKET_END_ID);
        OPERATORS.put("[", "SQUARE_BRACKET_START");
        OPERATORS.put("]", "SQUARE_BRACKET_END");
        OPERATORS.put(",", "COMMA");
        OPERATORS.put(".", "DOT");
        OPERATORS.put(":", "COLON");
        OPERATORS.put(";", FINISH_STATEMENT_ID);
        OPERATORS.put("" + SPACE, SPACE_ID);
        OPERATORS.put("" + CONTINUE_LINE, CONTINUE_LINE_ID);
        OPERATORS.put("" + NEWLINE, NEWLINE_ID);
        OPERATORS.put(":=", ASSIGN_ID);
        OPERATORS.put(">=", "LARGER_EQUALS");
        OPERATORS.put("<=", "SMALLER_EQUALS");
        OPERATORS.put("=/=", "NOT_EQUAL");
        OPERATORS.put("+=", "ASSIGN_PLUS");
        OPERATORS.put("-=", "ASSIGN_MINUS");
        OPERATORS.put("*=", "ASSIGN_TIMES");
        OPERATORS.put("/=", "ASSIGN_DIVIDE");
        OPERATORS.put("^=", "ASSIGN_POWER");

        if (DEBUG)
        {
            OPERATORS.put("$=", "TEMP");
            OPERATORS.put("=$=", "TEMP2");
        }
    }

    static
    {
        TOKENS = HashBiMap.create(KEYWORDS);
        TOKENS.putAll(OPERATORS);
        TOKENS_INVERSE = TOKENS.inverse();
        if (max(OPERATORS.keySet().stream().mapToInt(String::length).boxed().toList()) > MAX_OPERATOR_LENGTH)
            throw new IllegalArgumentException("MAX_OPERATOR_LENGTH smaller than length of largest operator!");
    }

    private Constants()
    {

    }

    public enum SYNTAX_TYPE
    {
        PROGRAM,
        METHOD,
        OPERATION,
        METHOD_BODY,
        TOKEN
    }
}
