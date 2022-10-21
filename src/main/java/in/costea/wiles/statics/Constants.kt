package `in`.costea.wiles.statics

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.isContainedIn
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import java.util.*
import java.util.function.Predicate
import kotlin.streams.toList

object Constants {
    const val DEBUG = true
    const val UNKNOWN_TOKEN = "UNKNOWN_TOKEN"
    const val START_BLOCK_ID = "START_BLOCK"
    const val END_BLOCK_ID = "END_BLOCK"
    const val SPACE_ID = "SPACE"
    const val NEWLINE_ID = "NEWLINE"
    const val ROUND_BRACKET_START_ID = "ROUND_BRACKET_START"
    const val ROUND_BRACKET_END_ID = "ROUND_BRACKET_END"
    private const val SQUARE_BRACKET_START_ID = "SQUARE_BRACKET_START"
    private const val SQUARE_BRACKET_END_ID = "SQUARE_BRACKET_END"
    const val METHOD_ID = "METHOD"
    private const val TERMINATOR_ID = "TERMINATOR"
    const val CONTINUE_LINE_ID = "CONTINUE_LINE"
    const val PLUS_ID = "PLUS"
    const val MINUS_ID = "MINUS"
    const val UNARY_ID = "UNARY_"
    private const val UNARY_PLUS_ID = UNARY_ID + PLUS_ID
    private const val UNARY_MINUS_ID = UNARY_ID + MINUS_ID
    const val TIMES_ID = "TIMES"
    private const val DIVIDE_ID = "DIVIDE"
    const val POWER_ID = "POWER"
    const val ASSIGN_ID = "ASSIGN"
    const val IDENTIFIER_START = "!"
    const val STRING_START = "@"
    const val NUM_START = "#"
    const val TYPEOF_ID = "TYPEOF"
    const val NOTHING_ID = "NOTHING"
    const val SEPARATOR_ID = "SEPARATOR"
    const val DO_ID = "DO"
    const val RIGHT_ARROW_ID = "RIGHT_ARROW"
    const val DECLARE_ID = "DECLARE"
    const val EQUALS_ID = "EQUALS"
    const val LARGER_ID = "LARGER"
    const val MUTABLE_ID = "MUTABLE"
    private const val SMALLER_ID = "SMALLER"
    private const val LARGER_EQUALS_ID = "LARGER_EQUALS"
    private const val SMALLER_EQUALS_ID = "SMALLER_EQUALS"
    private const val NOT_EQUAL_ID = "NOT_EQUAL"
    private const val ACCESS_ID = "ACCESS"
    private const val AND_ID = "AND"
    private const val APPLY_ID = "APPLY"
    const val OR_ID = "OR"
    const val NOT_ID = "NOT"
    private const val MAYBE_ID = "MAYBE"
    const val RETURN_ID = "RETURN"
    private const val TRUE_ID = "TRUE"
    private const val FALSE_ID = "FALSE"
    private const val IF_ID = "IF"
    private const val WHEN_ID = "WHEN"
    private const val ELSE_ID = "ELSE"
    private const val BREAK_ID = "BREAK"
    private const val CONTINUE_ID = "CONTINUE"
    private const val FOR_ID = "FOR"
    private const val WHILE_ID = "WHILE"
    const val UNNAMED_START = IDENTIFIER_START + "arg"
    const val INTERNAL_ERROR = "Internal error!"
    const val MAX_SYMBOL_LENGTH = 3
    const val STRING_DELIMITER = '"'
    const val DECIMAL_DELIMITER = '.'
    const val DIGIT_SEPARATOR = '_'
    const val COMMENT_START = '#'
    const val NEWLINE = '\n'
    const val SPACE = ' '
    const val CONTINUE_LINE = '\\'

    private val KEYWORDS: BiMap<String, String> = HashBiMap.create()
    private val SYMBOLS: BiMap<String, String> = HashBiMap.create()
    @JvmField
    val TYPES: BiMap<String, String> = HashBiMap.create()
    @JvmField
    val TOKENS: BiMap<String, String>
    @JvmField
    val TOKENS_INVERSE: BiMap<String, String>
    val PRECEDENCE : HashMap<String,Byte> = HashMap()
    val RIGHT_TO_LEFT : Set<Byte>

    @JvmField
    val INFIX_OPERATORS = setOf(
        PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID,
        EQUALS_ID, LARGER_ID, SMALLER_ID, LARGER_EQUALS_ID, SMALLER_EQUALS_ID, NOT_EQUAL_ID,
        ACCESS_ID, SEPARATOR_ID, AND_ID, OR_ID, APPLY_ID
    )
    @JvmField
    val PREFIX_OPERATORS = setOf(UNARY_PLUS_ID, UNARY_MINUS_ID, NOT_ID)
    @JvmField
    val STARTING_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID)
    @JvmField
    val ROUND_BRACKETS = setOf(ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID)
    @JvmField
    val TERMINATORS = setOf(NEWLINE_ID, TERMINATOR_ID)
    private val KEYWORD_LITERALS = setOf(TRUE_ID,FALSE_ID,NOTHING_ID)

    @JvmField
    val IS_IDENTIFIER = Predicate { x: String -> x.length > 1 && x.startsWith(IDENTIFIER_START) }
    private val IS_TEXT_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(STRING_START) }
    private val IS_NUMBER_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(NUM_START) }
    private val IS_KEYWORD_LITERAL= Predicate { x:String -> KEYWORD_LITERALS.contains(x) }
    @JvmField
    val IS_LITERAL: Predicate<String> = IS_IDENTIFIER.or(IS_TEXT_LITERAL).or(IS_NUMBER_LITERAL).or(IS_KEYWORD_LITERAL)

    @JvmField
    val EXPECT_TERMINATOR = tokenOf(isContainedIn(TERMINATORS))
        .dontIgnoreNewLine().withErrorMessage("End of line or semicolon expected!")

    init {
        PRECEDENCE[OR_ID] = -4
        PRECEDENCE[AND_ID] = -3
        PRECEDENCE[NOT_ID] = -2
        PRECEDENCE[EQUALS_ID] = -1
        PRECEDENCE[NOT_EQUAL_ID] = -1
        PRECEDENCE[LARGER_ID] = 0
        PRECEDENCE[SMALLER_ID] = 0
        PRECEDENCE[LARGER_EQUALS_ID] = 0
        PRECEDENCE[SMALLER_EQUALS_ID] = 0
        PRECEDENCE[PLUS_ID] = 1
        PRECEDENCE[MINUS_ID] = 1
        PRECEDENCE[TIMES_ID] = 2
        PRECEDENCE[DIVIDE_ID] = 2
        PRECEDENCE[UNARY_PLUS_ID] = 3
        PRECEDENCE[UNARY_MINUS_ID] = 3
        PRECEDENCE[POWER_ID] = 4
        PRECEDENCE[ACCESS_ID] = 5
        PRECEDENCE[APPLY_ID] = 5

        RIGHT_TO_LEFT = setOf(PRECEDENCE[NOT_ID]!!, PRECEDENCE[UNARY_PLUS_ID]!!, PRECEDENCE[POWER_ID]!!)

        KEYWORDS["nothing"] = NOTHING_ID
        KEYWORDS["method"] = METHOD_ID
        KEYWORDS["let"] = DECLARE_ID
        KEYWORDS["var"] = MUTABLE_ID
        KEYWORDS["if"] = IF_ID
        KEYWORDS["when"] = WHEN_ID
        KEYWORDS["otherwise"] = ELSE_ID
        KEYWORDS["for"] = FOR_ID
        KEYWORDS["while"] = WHILE_ID
        KEYWORDS["and"] = AND_ID
        KEYWORDS["or"] = OR_ID
        KEYWORDS["not"] = NOT_ID
        KEYWORDS["stop"] = BREAK_ID
        KEYWORDS["skip"] = CONTINUE_ID
        KEYWORDS["yield"] = RETURN_ID
        KEYWORDS["do"] = DO_ID
        KEYWORDS["begin"] = START_BLOCK_ID
        KEYWORDS["end"] = END_BLOCK_ID
        KEYWORDS["true"] = TRUE_ID
        KEYWORDS["false"] = FALSE_ID

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
        TYPES[NOTHING_ID] = NOTHING_ID

        SYMBOLS["+"] = PLUS_ID
        SYMBOLS["-"] = MINUS_ID
        SYMBOLS["*"] = TIMES_ID
        SYMBOLS["/"] = DIVIDE_ID
        SYMBOLS["^"] = POWER_ID
        SYMBOLS[":="] = ASSIGN_ID
        SYMBOLS["="] = EQUALS_ID
        SYMBOLS[">"] = LARGER_ID
        SYMBOLS["<"] = SMALLER_ID
        SYMBOLS[">="] = LARGER_EQUALS_ID
        SYMBOLS["<="] = SMALLER_EQUALS_ID
        SYMBOLS["=/="] = NOT_EQUAL_ID
        SYMBOLS["("] = ROUND_BRACKET_START_ID
        SYMBOLS[")"] = ROUND_BRACKET_END_ID
        SYMBOLS["["] = SQUARE_BRACKET_START_ID
        SYMBOLS["]"] = SQUARE_BRACKET_END_ID
        SYMBOLS[","] = SEPARATOR_ID
        SYMBOLS["."] = ACCESS_ID
        SYMBOLS[":"] = TYPEOF_ID
        SYMBOLS[";"] = TERMINATOR_ID
        SYMBOLS["->"] = RIGHT_ARROW_ID
        SYMBOLS["?"] = MAYBE_ID
        SYMBOLS["" + SPACE] = SPACE_ID
        SYMBOLS["" + CONTINUE_LINE] = CONTINUE_LINE_ID
        SYMBOLS["" + NEWLINE] = NEWLINE_ID
        if (DEBUG) {
            SYMBOLS["$="] = "TEMP"
            SYMBOLS["=$="] = "TEMP2"
        }
        TOKENS = HashBiMap.create(KEYWORDS)
        TOKENS.putAll(SYMBOLS)
        TOKENS_INVERSE = TOKENS.inverse()
        require(Collections.max(SYMBOLS.keys.stream().mapToInt { obj: String -> obj.length }
            .toList()) <= MAX_SYMBOL_LENGTH)
        {
            "MAX_SYMBOL_LENGTH smaller than length of largest symbol!"
        }
    }
}