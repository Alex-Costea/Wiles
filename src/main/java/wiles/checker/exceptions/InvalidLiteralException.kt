package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.INVALID_LITERAL_ERROR

class InvalidLiteralException(tokenLocation: TokenLocation)
    : AbstractCompilationException(INVALID_LITERAL_ERROR, tokenLocation)