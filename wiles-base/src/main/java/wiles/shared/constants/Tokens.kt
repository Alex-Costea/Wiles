package wiles.shared.constants

import wiles.shared.InternalErrorException
import wiles.shared.constants.Chars.CONTINUE_LINE
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
    const val BRACE_START_ID = "BRACE_START"
    const val BRACE_END_ID = "BRACE_END"
    const val METHOD_ID = "METHOD"
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
    const val TYPE_ANNOTATION_ID = "TYPE_ANNOTATION"
    const val SEPARATOR_ID = "SEPARATOR"
    const val DO_ID = "DO"
    const val RIGHT_ARROW_ID = "RIGHT_ARROW"
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
    const val FROM_ID = "FROM"
    const val TO_ID = "TO"
    const val WHILE_ID = "WHILE"
    const val ANON_ARG_ID = "ANON_ARG"
    const val MUTABLE_ID = "MUTABLE"
    const val WHEN_ID = "WHEN"
    const val IS_ID = "IS"
    const val AS_ID = "AS"
    const val TYPEDEF_ID = "TYPEDEF"
    const val DATA_ID = "DATA"

    const val TRUE_ID = "!true"
    const val FALSE_ID = "!false"
    const val NOTHING_ID = "!nothing"

    private val KEYWORDS: HashMap<String, String> = HashMap()
    private val SYMBOLS: HashMap<String, String> = HashMap()
    @JvmField
    val TOKENS: HashMap<String, String>
    @JvmField
    val INFIX_OPERATORS = setOf(
        PLUS_ID, MINUS_ID, TIMES_ID, DIVIDE_ID, POWER_ID,
        EQUALS_ID, LARGER_ID, SMALLER_ID, LARGER_EQUALS_ID, SMALLER_EQUALS_ID, NOT_EQUAL_ID,
        AND_ID, OR_ID, APPLY_ID, ACCESS_ID
    )
    @JvmField
    val PREFIX_OPERATORS = setOf(UNARY_PLUS_ID, UNARY_MINUS_ID, NOT_ID, MUTABLE_ID)
    @JvmField
    val STARTING_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID, MUTABLE_ID)
    @JvmField
    val TERMINATORS = setOf(NEWLINE_ID, TERMINATOR_ID)

    @JvmField
    val NEW_STATEMENT_START_KEYWORDS = setOf(
        DECLARE_ID, ELSE_ID, CONTINUE_ID, IS_ID, WHEN_ID, RETURN_ID, WHILE_ID, RIGHT_ARROW_ID,
        BREAK_ID, FOR_ID, DO_ID, START_BLOCK_ID, END_BLOCK_ID, BRACKET_END_ID, PAREN_END_ID,
        SEPARATOR_ID, IN_ID, FROM_ID, TO_ID, ASSIGN_ID, TERMINATOR_ID, NEWLINE_ID, IF_ID, BRACE_END_ID
    )

    init {
        KEYWORDS["fun"] = METHOD_ID
        KEYWORDS["let"] = DECLARE_ID
        KEYWORDS["var"] = VARIABLE_ID
        KEYWORDS["if"] = IF_ID
        KEYWORDS["default"] = ELSE_ID
        KEYWORDS["for"] = FOR_ID
        KEYWORDS["in"] = IN_ID
        KEYWORDS["from"] = FROM_ID
        KEYWORDS["to"] = TO_ID
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
        KEYWORDS["when"] = WHEN_ID
        KEYWORDS["is"] = IS_ID
        KEYWORDS["mut"] = MUTABLE_ID
        KEYWORDS["arg"] = ANON_ARG_ID
        KEYWORDS["as"] = AS_ID
        KEYWORDS["typedef"] = TYPEDEF_ID
        KEYWORDS["data"] = DATA_ID

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
        SYMBOLS["{"] = BRACE_START_ID
        SYMBOLS["}"] = BRACE_END_ID
        SYMBOLS[","] = SEPARATOR_ID
        SYMBOLS["."] = ACCESS_ID
        SYMBOLS[":"] = TYPE_ANNOTATION_ID
        SYMBOLS[";"] = TERMINATOR_ID
        SYMBOLS["->"] = RIGHT_ARROW_ID
        SYMBOLS["?"] = MAYBE_ID
        SYMBOLS["" + CONTINUE_LINE] = CONTINUE_LINE_ID
        SYMBOLS["\n"] = NEWLINE_ID

        TOKENS = hashMapOf()
        TOKENS.putAll(KEYWORDS)
        TOKENS.putAll(SYMBOLS)
        if(Collections.max(SYMBOLS.keys.stream().mapToInt { obj: String -> obj.length }.toList()) > MAX_SYMBOL_LENGTH)
            throw InternalErrorException(MAX_SYMBOL_TOO_LARGE_ERROR)
    }
}