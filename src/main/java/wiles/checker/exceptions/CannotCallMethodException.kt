package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.WRONG_ARGUMENTS_ERROR

class CannotCallMethodException(location: TokenLocation, message : String = WRONG_ARGUMENTS_ERROR) :
    AbstractCompilationException(message, location)