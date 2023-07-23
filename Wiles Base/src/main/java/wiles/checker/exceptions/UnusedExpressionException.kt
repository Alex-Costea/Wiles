package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.UNUSED_EXPRESSION_ERROR

class UnusedExpressionException(location : TokenLocation)
    : AbstractCompilationException(UNUSED_EXPRESSION_ERROR, location)