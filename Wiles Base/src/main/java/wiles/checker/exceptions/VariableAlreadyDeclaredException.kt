package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.VARIABLE_ALREADY_DECLARED_ERROR

class VariableAlreadyDeclaredException(location: TokenLocation)
    : AbstractCompilationException(VARIABLE_ALREADY_DECLARED_ERROR,location)