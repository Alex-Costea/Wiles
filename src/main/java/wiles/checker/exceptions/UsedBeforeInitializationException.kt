package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.USED_BEFORE_INITIALIZED_ERROR

class UsedBeforeInitializationException(location: TokenLocation) :
    AbstractCompilationException(USED_BEFORE_INITIALIZED_ERROR, location)