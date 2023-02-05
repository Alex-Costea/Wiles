package wiles.shared.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import wiles.shared.constants.Chars.CONTINUE_LINE
import wiles.shared.constants.ErrorMessages.MAX_SYMBOL_TOO_LARGE_ERROR
import wiles.shared.constants.Settings.DEBUG
import wiles.shared.constants.Settings.MAX_SYMBOL_LENGTH
import wiles.shared.constants.Settings.ROMANIAN_MODE
import wiles.shared.InternalErrorException
import java.util.*
import kotlin.collections.HashMap
import kotlin.streams.toList

object Tokens {
    const val ERROR_TOKEN = "ERROR_TOKEN"
    const val START_BLOCK_ID = "START_BLOCK"
    const val END_BLOCK_ID = "END_BLOCK"
    const val SPACE_ID = "SPACE"
    const val NEWLINE_ID = "NEWLINE"
    const val PAREN_START_ID = "PAREN_START"
    const val PAREN_END_ID = "PAREN_END"
    const val BRACKET_START_ID = "BRACKET_START"
    const val BRACKET_END_ID = "BRACKET_END"
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
    const val TYPEDEF_ID = "TYPEDEF"
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
    const val MAYBE_ID = "MAYBE"
    const val RETURN_ID = "RETURN"
    const val TRUE_ID = "TRUE"
    const val FALSE_ID = "FALSE"
    const val WHEN_ID = "WHEN"
    const val IF_ID = "IF"
    const val IS_ID = "IS"
    const val ELSE_ID = "ELSE"
    const val BREAK_ID = "BREAK"
    const val CONTINUE_ID = "CONTINUE"
    const val FOR_ID = "FOR"
    const val IN_ID = "IN"
    const val FROM_ID = "FROM"
    const val TO_ID = "TO"
    const val WHILE_ID = "WHILE"
    const val ELEM_ACCESS_ID = "ELEM_ACCESS"
    const val CASE_ID = "CASE"
    const val ANON_ARG_ID = "ANON_ARG"
    const val THEN_ID = "THEN"

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
        AND_ID, OR_ID, APPLY_ID, ACCESS_ID, ELEM_ACCESS_ID
    )
    @JvmField
    val PREFIX_OPERATORS = setOf(UNARY_PLUS_ID, UNARY_MINUS_ID, NOT_ID)
    @JvmField
    val STARTING_OPERATORS = setOf(PLUS_ID, MINUS_ID, NOT_ID)
    @JvmField
    val PARENS = setOf(PAREN_START_ID, PAREN_END_ID, BRACKET_START_ID)
    @JvmField
    val TERMINATORS = setOf(NEWLINE_ID, TERMINATOR_ID)
    val KEYWORD_LITERALS = setOf(TRUE_ID, FALSE_ID, NOTHING_ID)

    @JvmField
    val NEW_STATEMENT_START_KEYWORDS = setOf(
        NOTHING_ID, DECLARE_ID, WHEN_ID, CASE_ID, ELSE_ID, CONTINUE_ID,
        RETURN_ID, WHILE_ID, BREAK_ID, FOR_ID, DO_ID, START_BLOCK_ID, END_BLOCK_ID, BRACKET_END_ID, PAREN_END_ID,
        SEPARATOR_ID, IN_ID, FROM_ID, TO_ID, IS_ID, ASSIGN_ID, TERMINATOR_ID, NEWLINE_ID, IF_ID, THEN_ID
    )

    init {
        KEYWORDS[if(!ROMANIAN_MODE) "nothing" else "nimic"] = NOTHING_ID
        KEYWORDS["fun"] = METHOD_ID
        KEYWORDS[if(!ROMANIAN_MODE) "let" else "fie"] = DECLARE_ID
        KEYWORDS["var"] = MUTABLE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "if" else "dacă"] = IF_ID
        KEYWORDS[if(!ROMANIAN_MODE) "when" else "când"] = WHEN_ID
        KEYWORDS[if(!ROMANIAN_MODE) "is" else "este"] = IS_ID
        KEYWORDS[if(!ROMANIAN_MODE) "then" else "atunci"] = THEN_ID
        KEYWORDS[if(!ROMANIAN_MODE) "case" else "caz"] = CASE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "default" else "altfel"] = ELSE_ID
        KEYWORDS[if(!ROMANIAN_MODE) "for" else "pentru"] = FOR_ID
        KEYWORDS[if(!ROMANIAN_MODE) "in" else "în"] = IN_ID
        KEYWORDS[if(!ROMANIAN_MODE) "from" else "de_la"] = FROM_ID
        KEYWORDS[if(!ROMANIAN_MODE) "to" else "la"] = TO_ID
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
        KEYWORDS["arg"] = ANON_ARG_ID

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
        SYMBOLS["@"] = ELEM_ACCESS_ID
        SYMBOLS[","] = SEPARATOR_ID
        SYMBOLS["."] = ACCESS_ID
        SYMBOLS[":"] = TYPEDEF_ID
        SYMBOLS[";"] = TERMINATOR_ID
        SYMBOLS["->"] = RIGHT_ARROW_ID
        SYMBOLS["?"] = MAYBE_ID
        SYMBOLS[" "] = SPACE_ID
        SYMBOLS["" + CONTINUE_LINE] = CONTINUE_LINE_ID
        SYMBOLS["\n"] = NEWLINE_ID
        if (DEBUG) {
            SYMBOLS["$="] = "TEMP"
            SYMBOLS["=$="] = "TEMP2"
        }
        TOKENS = HashBiMap.create(KEYWORDS)
        TOKENS.putAll(SYMBOLS)
        TOKENS_INVERSE = TOKENS.inverse()
        if(Collections.max(SYMBOLS.keys.stream().mapToInt { obj: String -> obj.length }.toList()) > MAX_SYMBOL_LENGTH)
            throw InternalErrorException(MAX_SYMBOL_TOO_LARGE_ERROR)
    }
}