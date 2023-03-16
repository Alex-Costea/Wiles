package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.WRONG_OPERATION_ERROR

class WrongOperationException(location: TokenLocation, type1 : String, type2 : String)
    : AbstractCompilationException(WRONG_OPERATION_ERROR.format(type1,type2), location)