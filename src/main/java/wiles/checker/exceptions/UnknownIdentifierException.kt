package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.UNKNOWN_IDENTIFIER_ERROR

class UnknownIdentifierException(location: TokenLocation) :
    AbstractCompilationException(UNKNOWN_IDENTIFIER_ERROR, location)