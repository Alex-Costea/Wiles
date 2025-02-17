package wiles.shared.constants

object ErrorMessages {
    const val LINE_SYMBOL = "\n>>> "
    const val COMPILATION_FAILED_ERROR = "${LINE_SYMBOL}COMPILATION FAILED$LINE_SYMBOL"

    // Parsing errors
    const val TOKEN_EXPECTED_ERROR = "The token is missing or incorrect. Did you mean %s?"
    const val IDENTIFIER_EXPECTED_ERROR = "The identifier name is invalid or missing."
    const val EXPRESSION_EXPECTED_ERROR = "The expression is invalid or missing."
    const val UNEXPECTED_OPENING_PAREN_ERROR = "Expected a closing parenthesis here."
    const val OPERATOR_EXPECTED_ERROR = "Expected an operator."
    const val EXPRESSION_UNFINISHED_ERROR = "The expression is unfinished."
    const val INVALID_EXPRESSION_ERROR = "The expression starting from this point is invalid."
    const val INVALID_STATEMENT_ERROR = "The statement is invalid."
    const val STRING_UNFINISHED_ERROR = "The text is unfinished."
    const val END_OF_STATEMENT_EXPECTED_ERROR = "Expected end of statement. " +
            "Please use the `;` symbol or start a new line."
    const val CONST_CANT_BE_VAR_ERROR = "This value cannot be both constant and variable at the same time."
    const val EXPECTED_INITIALIZATION_ERROR = "Expected value initialization."

    // Processing errors
    const val UNKNOWN_IDENTIFIER_ERROR = "Identifier unknown."

    // Internal errors
    const val INTERNAL_ERROR = "An internal error has occurred. "
    const val IO_ERROR = "An error has occurred while trying to load the source code file."
    const val CANNOT_BE_PROCESSED_ERROR = "Cannot be processed."
    const val NOT_YET_IMPLEMENTED_ERROR = "Not yet implemented."
    const val MAX_SYMBOL_TOO_LARGE_ERROR = "MAX_SYMBOL_LENGTH is smaller than the length of the largest symbol."
    const val FROZEN_ERROR = "The object's state cannot be edited after it has been frozen."
    const val ERROR_MESSAGE_EXPECTED_ERROR = "Expected an error message."
    const val WHEN_REMOVE_EXPECTED_ERROR = "When to remove token parameter expected."
}