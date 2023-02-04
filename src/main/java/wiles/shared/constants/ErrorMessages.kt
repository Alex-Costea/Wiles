package wiles.shared.constants

import wiles.shared.constants.Tokens.TERMINATOR_ID
import wiles.shared.constants.Tokens.TOKENS_INVERSE

object ErrorMessages {
    const val LINE_SYMBOL = "\n>>> "
    const val COMPILATION_FAILED_ERROR ="${LINE_SYMBOL}COMPILATION FAILED$LINE_SYMBOL"
    const val TOKEN_EXPECTED_ERROR = "Token is incorrect or missing. Did you mean: \"%s\"?"
    const val IDENTIFIER_EXPECTED_ERROR = "Invalid or missing identifier name."
    const val IDENTIFIER_TOO_LONG_ERROR = "Only one identifier expected here."
    const val TYPE_EXPECTED_ERROR = "Type definition is unknown."
    const val EXPRESSION_EXPECTED_ERROR = "Invalid or missing expression"
    const val UNEXPECTED_OPENING_BRACKET_ERROR = "Closing bracket expected here."
    const val OPERATOR_EXPECTED_ERROR = "Operator expected."
    const val EXPRESSION_UNFINISHED_ERROR = "Expression unfinished."
    const val INVALID_EXPRESSION_ERROR = "Invalid expression starting from this point."
    const val INVALID_STATEMENT_ERROR = "Invalid statement."
    const val STRING_UNFINISHED_ERROR = "Text unfinished."
    const val INVALID_LEFT_EXCEPTION = "Left side of assignment invalid."
    const val NOT_ENOUGH_TYPES_EXCEPTION = "Not enough types have been specified."
    val END_OF_STATEMENT_EXPECTED_ERROR = "End of statement expected. " +
            "Please use the ${TOKENS_INVERSE[TERMINATOR_ID]!!} symbol or start a new line."

    //Internal errors
    const val INTERNAL_ERROR = "An internal error occurred. "
    const val IO_ERROR = "An error occurred while trying to load the source code file."
    const val CANNOT_BE_PROCESSED_ERROR = "Cannot be processed."
    const val NOT_YET_IMPLEMENTED_ERROR = "Not yet implemented."
    const val MAX_SYMBOL_TOO_LARGE_ERROR = "MAX_SYMBOL_LENGTH smaller than length of largest symbol."
    const val FROZEN_ERROR = "Object state cannot be edited after being frozen."
    const val ERROR_MESSAGE_EXPECTED_ERROR = "An error message is expected."
    const val WHEN_REMOVE_EXPECTED_ERROR = "When to remove token parameter expected."
}