package `in`.costea.wiles.constants

object ErrorMessages {
    const val INTERNAL_ERROR = "An internal error occurred."
    const val IO_ERROR = "Error loading input file."
    const val TOKEN_EXPECTED_ERROR = "Expected to find the token \"%s\" here."
    const val IDENTIFIER_EXPECTED_ERROR = "An identifier is expected here."
    const val AT_LINE_INDEX = "%s at line: %d index: %d"
    const val LITERAL_EXPECTED_ERROR = "A literal value is expected here."
    const val TYPE_EXPECTED_ERROR = "A type is expected here."
    const val EXPRESSION_EXPECTED_ERROR = "An expression is expected here."
    const val NON_MATCHING_BRACKETS_ERROR = "Non-matching brackets."
    const val OPERATOR_EXPECTED_ERROR = "An operator is expected here."
    const val EXPRESSION_UNFINISHED_ERROR = "The expression is unfinished."
    const val UNEXPECTED_TOKEN_ERROR = "This token is not expected here."
    const val STRING_UNFINISHED_ERROR = "Text unfinished."
    const val CANNOT_BE_PROCESSED_ERROR = "Cannot be processed."
    const val NOT_YET_IMPLEMENTED_ERROR = "Not yet implemented."
    const val MAX_SYMBOL_TOO_LARGE = "MAX_SYMBOL_LENGTH smaller than length of largest symbol."
    const val END_OF_STATEMENT_EXPECTED = "End of statement expected. " +
    "Please use the semicolon symbol or start a new line."
    const val CANNOT_EDIT = "Object state cannot be edited after being frozen."
    const val ERROR_MESSAGE_EXPECTED = "An error message is expected."
    const val WHEN_REMOVE_EXPECTED = "When to remove token parameter expected."
    const val LINE_SYMBOL = "\n>>> "
    const val COMPILATION_FAILED ="${LINE_SYMBOL}COMPILATION FAILED${LINE_SYMBOL}"
}