package `in`.costea.wiles.constants

object ErrorMessages {
    const val INTERNAL_ERROR = "Internal error!"
    const val IO_ERROR = "Error loading input file!"
    const val TOKEN_EXPECTED_ERROR = "Token \"%s\" expected!"
    const val IDENTIFIER_EXPECTED_ERROR = "Identifier expected!"
    const val RIGHT_SIDE_EXPECTED_ERROR = "Right side of declaration expected!"
    const val AT_LINE_INDEX = "%s at line: %d index: %d"
    const val LITERAL_EXPECTED_ERROR = "Literal expected!"
    const val TYPE_EXPECTED_ERROR = "Type expected!"
    const val EXPRESSION_EXPECTED_ERROR = "Expression expected!"
    const val END_TOKEN_NOT_ALLOWED_ERROR = "End token not allowed here!"
    const val NON_MATCHING_BRACKETS_ERROR = "Brackets don't close properly!"
    const val OPERATOR_EXPECTED_ERROR = "Operator expected!"
    const val IDENTIFIER_OR_UNARY_OPERATOR_EXPECTED_ERROR = "Identifier or unary operator expected!"
    const val EXPRESSION_UNFINISHED_ERROR = "Expression unfinished!"
    const val UNEXPECTED_TOKEN_ERROR = "Unexpected token: %s"
    const val STRING_UNFINISHED_ERROR = "Text unfinished: \"%s"
    const val CANNOT_BE_PROCESSED_ERROR = "Cannot be processed!"
    const val NOT_YET_IMPLEMENTED_ERROR = "Not yet implemented!"
    const val MAX_SYMBOL_TOO_LARGE = "MAX_SYMBOL_LENGTH smaller than length of largest symbol!"
}