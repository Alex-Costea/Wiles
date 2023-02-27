package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.IDENTIFIER_TOO_LONG_ERROR

class ExpectedIdentifierException(tokenLocation: TokenLocation) :
    AbstractCompilationException(IDENTIFIER_TOO_LONG_ERROR, tokenLocation)