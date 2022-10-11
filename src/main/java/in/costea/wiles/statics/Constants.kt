package `in`.costea.wiles.statics

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import java.util.*
import java.util.function.Predicate

object Constants {
    const val DEBUG = true
    const val UNKNOWN_TOKEN = "ERROR_TOKEN"
    const val START_BLOCK_ID = "START_BLOCK"
    const val END_BLOCK_ID = "END_BLOCK"
    const val SPACE_ID = "SPACE"
    const val NEWLINE_ID = "NEWLINE"
    const val ROUND_BRACKET_START_ID = "ROUND_BRACKET_START"
    const val ROUND_BRACKET_END_ID = "ROUND_BRACKET_END"
    const val SQUARE_BRACKET_START_ID = "SQUARE_BRACKET_START"
    const val SQUARE_BRACKET_END_ID = "SQUARE_BRACKET_END"
    const val METHOD_ID = "METHOD"
    const val STATEMENT_TERMINATOR_ID = "TERMINATOR"
    const val BACKSLASH_ID = "BACKSLASH"
    const val PLUS_ID = "PLUS"
    const val MINUS_ID = "MINUS"
    const val TIMES_ID = "TIMES"
    private const val DIVIDE_ID = "DIVIDE"
    const val POWER_ID = "POWER"
    const val ASSIGN_ID = "ASSIGN"
    const val IDENTIFIER_START = "!"
    const val STRING_START = "@"
    const val NUM_START = "#"
    const val COLON_ID = "COLON"
    const val NOTHING_ID = "NOTHING"
    const val COMMA_ID = "COMMA"
    const val DO_ID = "DO"
    const val RIGHT_ARROW_ID="RIGHT_ARROW"
    const val DECLARE_ID="DECLARE"
    const val ASSIGNMENT_START_ID = "ASSIGNMENT_START"
    private const val EQUALS_ID = "EQUALS"
    private const val LARGER_ID = "LARGER"
    private const val SMALLER_ID = "SMALLER"
    private const val LARGER_EQUALS_ID = "LARGER_EQUALS"
    private const val SMALLER_EQUALS_ID = "SMALLER_EQUALS"
    private const val NOT_EQUAL_ID = "NOT_EQUAL"
    private const val DOT_ID = "DOT"
    private const val AND_ID = "AND"
    private const val OR_ID = "OR"
    private const val NOT_ID = "NOT"
    const val ANON_STARTS_WITH=IDENTIFIER_START+"arg"


    @JvmField
    val KEYWORDS: BiMap<String, String> = HashBiMap.create()

    @JvmField
    val OPERATORS: BiMap<String, String> = HashBiMap.create()

    @JvmField
    val TYPES: BiMap<String, String> = HashBiMap.create()
    private val TOKENS: BiMap<String, String>

    @JvmField
    val TOKENS_INVERSE: BiMap<String, String>
    const val MAX_OPERATOR_LENGTH = 3
    const val STRING_DELIMITER = '"'
    const val DECIMAL_DELIMITER = '.'
    const val DIGIT_SEPARATOR = '_'
    const val COMMENT_START = '#'
    const val NEWLINE = '\n'
    const val SPACE = ' '
    const val CONTINUE_LINE = '\\'

    @JvmField
    val INFIX_OPERATORS = setOf(
            PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID,
            EQUALS_ID, LARGER_ID, SMALLER_ID, LARGER_EQUALS_ID, SMALLER_EQUALS_ID, NOT_EQUAL_ID,
            DOT_ID, COMMA_ID, AND_ID, OR_ID)

    @JvmField
    val UNARY_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID)

    @JvmField
    val BRACKETS = setOf(ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID,
            SQUARE_BRACKET_START_ID, SQUARE_BRACKET_END_ID)

    @JvmField
    val STATEMENT_TERMINATORS = setOf(NEWLINE_ID, STATEMENT_TERMINATOR_ID)


    @JvmField
    val IS_IDENTIFIER = Predicate { x: String -> x.length > 1 && x.startsWith(IDENTIFIER_START) }
    private val IS_TEXT_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(STRING_START) }
    private val IS_NUMBER_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(NUM_START) }

    @JvmField
    val IS_LITERAL: Predicate<String> = IS_IDENTIFIER.or(IS_TEXT_LITERAL).or(IS_NUMBER_LITERAL)

    init {
        KEYWORDS["nothing"] = NOTHING_ID
        KEYWORDS["method"] = METHOD_ID
        KEYWORDS["let"] = DECLARE_ID
        KEYWORDS["var"] = "MUTABLE"
        KEYWORDS["if"] = "IF"
        KEYWORDS["when"] = "WHEN"
        KEYWORDS["otherwise"] = "ELSE"
        KEYWORDS["for"] = "FOR"
        KEYWORDS["while"] = "WHILE"
        KEYWORDS["and"] = AND_ID
        KEYWORDS["or"] = OR_ID
        KEYWORDS["not"] = NOT_ID
        KEYWORDS["stop"] = "BREAK"
        KEYWORDS["skip"] = "CONTINUE"
        KEYWORDS["yield"] = "RETURN"
        KEYWORDS["do"] = "DO"
        KEYWORDS["begin"] = START_BLOCK_ID
        KEYWORDS["end"] = END_BLOCK_ID
        KEYWORDS["set"] = ASSIGNMENT_START_ID

        TYPES["!bit"] = "BOOLEAN"
        TYPES["!byte"] = "INT8"
        TYPES["!smallint"] = "INT16"
        TYPES["!int"] = "INT32"
        TYPES["!bigint"] = "INT64"
        TYPES["!text"] = "STRING"
        TYPES["!rational"] = "DOUBLE"
        TYPES["!list"] = "ARRAY_LIST"
        TYPES["!either"] = "EITHER"
        TYPES["!range"] = "RANGE"
        TYPES[NOTHING_ID] = "NOTHING"

        OPERATORS["+"] = PLUS_ID
        OPERATORS["-"] = MINUS_ID
        OPERATORS["*"] = TIMES_ID
        OPERATORS["/"] = DIVIDE_ID
        OPERATORS["^"] = POWER_ID
        OPERATORS[":="] = ASSIGN_ID
        OPERATORS["="] = EQUALS_ID
        OPERATORS[">"] = LARGER_ID
        OPERATORS["<"] = SMALLER_ID
        OPERATORS[">="] = LARGER_EQUALS_ID
        OPERATORS["<="] = SMALLER_EQUALS_ID
        OPERATORS["=/="] = NOT_EQUAL_ID
        OPERATORS["("] = ROUND_BRACKET_START_ID
        OPERATORS[")"] = ROUND_BRACKET_END_ID
        OPERATORS["["] = SQUARE_BRACKET_START_ID
        OPERATORS["]"] = SQUARE_BRACKET_END_ID
        OPERATORS[","] = COMMA_ID
        OPERATORS["."] = DOT_ID
        OPERATORS[":"] = COLON_ID
        OPERATORS[";"] = STATEMENT_TERMINATOR_ID
        OPERATORS["-->"] = RIGHT_ARROW_ID
        OPERATORS["" + SPACE] = SPACE_ID
        OPERATORS["" + CONTINUE_LINE] = BACKSLASH_ID
        OPERATORS["" + NEWLINE] = NEWLINE_ID
        if (DEBUG) {
            OPERATORS["$="] = "TEMP"
            OPERATORS["=$="] = "TEMP2"
        }
        TOKENS = HashBiMap.create(KEYWORDS)
        TOKENS.putAll(OPERATORS)
        TOKENS_INVERSE = TOKENS.inverse()
        require(Collections.max(OPERATORS.keys.stream().mapToInt { obj: String? ->
            obj?.length ?: 0
        }.boxed().toList()) <= MAX_OPERATOR_LENGTH)
        { "MAX_OPERATOR_LENGTH smaller than length of largest operator!" }
    }
}