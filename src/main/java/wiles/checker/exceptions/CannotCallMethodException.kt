package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.WRONG_ARGUMENTS_ERROR

class CannotCallMethodException(location: TokenLocation, reason : String) :
    AbstractCompilationException(WRONG_ARGUMENTS_ERROR + reason, location)