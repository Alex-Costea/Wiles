package wiles.shared.constants

import wiles.shared.InternalErrorException
import wiles.shared.constants.ErrorMessages.MAX_SYMBOL_TOO_LARGE_ERROR
import wiles.shared.constants.Settings.MAX_SYMBOL_LENGTH
import java.util.*
import kotlin.streams.toList

object Tokens {
    const val ERROR_TOKEN = "ERROR_TOKEN"
    const val START_BLOCK_ID = "START_BLOCK"
    const val END_BLOCK_ID = "END_BLOCK"
    const val NEWLINE_ID = "NEWLINE"
    const val PAREN_START_ID = "PAREN_START"
    const val PAREN_END_ID = "PAREN_END"
    const val BRACKET_START_ID = "BRACKET_START"
    const val BRACKET_END_ID = "BRACKET_END"
    const val DICT_START_ID = "DICT_START"
    const val DICT_END_ID = "DICT_END"
    const val DATA_START_ID = "DATA_START"
    const val DATA_END_ID = "DATA_END"
    const val FUNC_ID = "FUNC"
    const val TERMINATOR_ID = "TERMINATOR"
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
    const val KEYWORD_START = "%"
    const val ANNOTATE_ID = "ANNOTATE"
    const val SEPARATOR_ID = "SEPARATOR"
    const val DO_ID = "DO"
    const val DECLARE_ID = "DECLARE"
    const val EQUALS_ID = "EQUALS"
    const val LARGER_ID = "LARGER"
    const val VARIABLE_ID = "VARIABLE"
    const val SMALLER_ID = "SMALLER"
    const val LARGER_EQUALS_ID = "LARGER_EQUALS"
    const val SMALLER_EQUALS_ID = "SMALLER_EQUALS"
    const val NOT_EQUAL_ID = "NOT_EQUAL"
    const val ACCESS_ID = "ACCESS"
    const val AND_ID = "AND"
    const val APPLY_ID = "APPLY"
    const val AT_KEY_ID = "AT_KEY"
    const val OR_ID = "OR"
    const val NOT_ID = "NOT"
    const val MAYBE_ID = "MAYBE"
    const val RETURN_ID = "RETURN"
    const val IF_ID = "IF"
    const val ELSE_ID = "ELSE"
    const val BREAK_ID = "BREAK"
    const val CONTINUE_ID = "CONTINUE"
    const val FOR_ID = "FOR"
    const val IN_ID = "IN"
    const val WHILE_ID = "WHILE"
    const val ANON_ARG_ID = "ANON_ARG"
    const val MUTABLE_ID = "MUTABLE"
    const val GLOBAL_ID = "GLOBAL"
    const val UNION_ID = "UNION"
    const val YIELDS_ID = "YIELDS"
    const val CONST_ID = "CONST"

    //literals
    const val TRUE_ID = "!true"
    const val FALSE_ID = "!false"
    const val NOTHING_ID = "!nothing"
    const val ANYTHING_ID = "!anything"
    const val INT_ID = "!int"
    const val STRING_ID = "!text"
    const val DECIMAL_ID = "!decimal"
    const val LIST_ID = "!list"
    const val DICT_ID = "!dict"
    const val DATA_ID = "!data"
    const val MUTABLE_TYPE_ID = "!mutable"
    const val CONST_TYPE_ID = "!constant"
    const val RANGE_ID = "!range"
    const val TYPE_ID = "!type"
    const val INFINITY_ID = "!Infinity"

    private val KEYWORDS: HashMap<String, String> = HashMap()
    private val SYMBOLS: HashMap<String, String> = HashMap()
    @JvmField
    val TOKENS: HashMap<String, String>
    @JvmField
    val INFIX_OPERATORS = setOf(
        PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID,
        EQUALS_ID, LARGER_ID, SMALLER_ID, LARGER_EQUALS_ID, SMALLER_EQUALS_ID, NOT_EQUAL_ID,
        AND_ID, OR_ID, APPLY_ID, ACCESS_ID, UNION_ID, AT_KEY_ID
    )
    @JvmField
    val PREFIX_OPERATORS = setOf(UNARY_PLUS_ID, UNARY_MINUS_ID, NOT_ID, MUTABLE_ID)
    @JvmField
    val SUFFIX_OPERATORS = setOf(MAYBE_ID)
    @JvmField
    val INFIX_SUFFIX_OPERATORS = INFIX_OPERATORS + SUFFIX_OPERATORS
    @JvmField
    val ALL_OPERATORS = PREFIX_OPERATORS + INFIX_SUFFIX_OPERATORS
    @JvmField
    val STARTING_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID, MUTABLE_ID)
    @JvmField
    val TERMINATORS = setOf(NEWLINE_ID, TERMINATOR_ID)

    @JvmField
    val KEYWORDS_INDICATING_NEW_EXPRESSION = setOf(
        DECLARE_ID, ELSE_ID, CONTINUE_ID, RETURN_ID, WHILE_ID, ANNOTATE_ID, DATA_END_ID, YIELDS_ID,
        BREAK_ID, FOR_ID, DO_ID, START_BLOCK_ID, END_BLOCK_ID, BRACKET_END_ID, PAREN_END_ID,
        SEPARATOR_ID, IN_ID, ASSIGN_ID, TERMINATOR_ID, NEWLINE_ID, IF_ID, DICT_END_ID
    )

    init {
        KEYWORDS["fun"] = FUNC_ID
        KEYWORDS["let"] = DECLARE_ID
        KEYWORDS["var"] = VARIABLE_ID
        KEYWORDS["if"] = IF_ID
        KEYWORDS["default"] = ELSE_ID
        KEYWORDS["for"] = FOR_ID
        KEYWORDS["in"] = IN_ID
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
        KEYWORDS["arg"] = ANON_ARG_ID
        KEYWORDS["def"] = GLOBAL_ID
        KEYWORDS["const"] = CONST_ID

        KEYWORDS["int"] = INT_ID
        KEYWORDS["text"] = STRING_ID
        KEYWORDS["decimal"] = DECIMAL_ID
        KEYWORDS["list"] = LIST_ID
        KEYWORDS["anything"] = ANYTHING_ID
        KEYWORDS["dict"] = DICT_ID
        KEYWORDS["data"] = DATA_ID
        KEYWORDS["mutable"] = MUTABLE_TYPE_ID
        KEYWORDS["constant"] = CONST_TYPE_ID
        KEYWORDS["range"] = RANGE_ID
        KEYWORDS["type"] = TYPE_ID
        KEYWORDS["Infinity"] = INFINITY_ID

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
        SYMBOLS["("] = PAREN_START_ID
        SYMBOLS[")"] = PAREN_END_ID
        SYMBOLS["["] = BRACKET_START_ID
        SYMBOLS["]"] = BRACKET_END_ID
        SYMBOLS["{"] = DICT_START_ID
        SYMBOLS["}"] = DICT_END_ID
        SYMBOLS["<<"] = DATA_START_ID
        SYMBOLS[">>"] = DATA_END_ID
        SYMBOLS[","] = SEPARATOR_ID
        SYMBOLS["."] = ACCESS_ID
        SYMBOLS[":"] = ANNOTATE_ID
        SYMBOLS[";"] = TERMINATOR_ID
        SYMBOLS["?"] = MAYBE_ID
        SYMBOLS["~"] = MUTABLE_ID
        SYMBOLS["|"] = UNION_ID
        SYMBOLS["->"] = YIELDS_ID
        SYMBOLS["\\"] = CONTINUE_LINE_ID
        SYMBOLS["\n"] = NEWLINE_ID

        TOKENS = hashMapOf()
        TOKENS.putAll(KEYWORDS)
        TOKENS.putAll(SYMBOLS)
        if(Collections.max(SYMBOLS.keys.stream().mapToInt { obj: String -> obj.length }.toList()) > MAX_SYMBOL_LENGTH)
            throw InternalErrorException(MAX_SYMBOL_TOO_LARGE_ERROR)
    }
}