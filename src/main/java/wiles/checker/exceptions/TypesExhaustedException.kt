package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.TYPES_EXHAUSTED_ERROR

class TypesExhaustedException(tokenLocation: TokenLocation)
    : AbstractCompilationException(TYPES_EXHAUSTED_ERROR, tokenLocation)