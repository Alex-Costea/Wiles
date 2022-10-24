package `in`.costea.wiles.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import `in`.costea.wiles.constants.Chars.CONTINUE_LINE
import `in`.costea.wiles.constants.Chars.NEWLINE
import `in`.costea.wiles.constants.Chars.SPACE
import `in`.costea.wiles.constants.ErrorMessages.MAX_SYMBOL_TOO_LARGE
import `in`.costea.wiles.constants.Settings.DEBUG
import `in`.costea.wiles.constants.Settings.MAX_SYMBOL_LENGTH
import `in`.costea.wiles.constants.Settings.ROMANIAN_MODE
import java.util.*
import kotlin.collections.HashMap
import kotlin.streams.toList

object Tokens {
    const val ERROR_TOKEN = "ERROR_TOKEN"
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
    const val UNARY_PLUS_ID = UNARY_ID + PLUS_ID
    const val UNARY_MINUS_ID = UNARY_ID + MINUS_ID
    const val TIMES_ID = "TIMES"
    const val DIVIDE_ID = "DIVIDE"
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
    const val SMALLER_ID = "SMALLER"
    const val LARGER_EQUALS_ID = "LARGER_EQUALS"
    const val SMALLER_EQUALS_ID = "SMALLER_EQUALS"
    const val NOT_EQUAL_ID = "NOT_EQUAL"
    const val ACCESS_ID = "ACCESS"
    const val AND_ID = "AND"
    const val APPLY_ID = "APPLY"
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

    private val KEYWORDS: HashMap<String, String> = HashMap()
    private val SYMBOLS: HashMap<String, String> = HashMap()
    @JvmField
    val TOKENS: BiMap<String, String>
    @JvmField
    val TOKENS_INVERSE: BiMap<String, String>

    @JvmField
    val INFIX_OPERATORS = setOf(
        PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID,
        EQUALS_ID, LARGER_ID, SMALLER_ID, LARGER_EQUALS_ID, SMALLER_EQUALS_ID, NOT_EQUAL_ID,
        ACCESS_ID, SEPARATOR_ID, AND_ID, OR_ID, APPLY_ID)
    @JvmField
    val PREFIX_OPERATORS = setOf(UNARY_PLUS_ID, UNARY_MINUS_ID, NOT_ID)
    @JvmField
    val STARTING_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID)
    @JvmField
    val ROUND_BRACKETS = setOf(ROUND_BRACKET_START_ID, ROUND_BRACKET_END_ID)
    @JvmField
    val TERMINATORS = setOf(NEWLINE_ID, TERMINATOR_ID)
    val KEYWORD_LITERALS = setOf(TRUE_ID,FALSE_ID,NOTHING_ID)

    init {
        KEYWORDS[if(!ROMANIAN_MODE) "nothing" else "nimic"] = NOTHING_ID
        KEYWORDS[if(!ROMANIAN_MODE) "method" else "metodă"] = METHOD_ID
        KEYWORDS[if(!ROMANIAN_MODE) "let" else "fie"] = DECLARE_ID
        KEYWORDS["var"] = MUTABLE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "if" else "dacă"] = IF_ID
        KEYWORDS[if(!ROMANIAN_MODE) "when" else "când"] = WHEN_ID
        KEYWORDS[if(!ROMANIAN_MODE) "otherwise" else "altfel"] = ELSE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "for" else "pentru"] = FOR_ID
        KEYWORDS[if(!ROMANIAN_MODE) "while" else "cât_timp"] = WHILE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "and" else "și"] = AND_ID
        KEYWORDS[if(!ROMANIAN_MODE) "or" else "sau"] = OR_ID
        KEYWORDS[if(!ROMANIAN_MODE) "not" else "nu"] = NOT_ID
        KEYWORDS[if(!ROMANIAN_MODE) "stop" else "oprește"] = BREAK_ID
        KEYWORDS[if(!ROMANIAN_MODE) "skip" else "sari_peste"] = CONTINUE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "yield" else "produce"] = RETURN_ID
        KEYWORDS[if(!ROMANIAN_MODE) "do" else "fă"] = DO_ID
        KEYWORDS[if(!ROMANIAN_MODE) "begin" else "început"] = START_BLOCK_ID
        KEYWORDS[if(!ROMANIAN_MODE) "end" else "sfârșit"] = END_BLOCK_ID
        KEYWORDS[if(!ROMANIAN_MODE) "true" else "adevărat"] = TRUE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "false" else "fals"] = FALSE_ID

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
            .toList()) <= MAX_SYMBOL_LENGTH) { MAX_SYMBOL_TOO_LARGE }
    }
}