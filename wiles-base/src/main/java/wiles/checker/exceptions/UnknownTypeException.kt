package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.UNKNOWN_TYPE_ERROR

class UnknownTypeException(location: TokenLocation) :
    AbstractCompilationException(UNKNOWN_TYPE_ERROR, location)