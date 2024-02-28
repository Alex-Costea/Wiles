package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.GENERIC_ALREADY_DEFINED_ERROR

class GenericAlreadyDefinedException(tokenLocation: TokenLocation)
    : AbstractCompilationException(GENERIC_ALREADY_DEFINED_ERROR, tokenLocation
)