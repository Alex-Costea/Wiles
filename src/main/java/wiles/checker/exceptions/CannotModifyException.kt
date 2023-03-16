package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.CANNOT_MODIFY_IMMUTABLE_ERROR

class CannotModifyException(location: TokenLocation) : AbstractCompilationException(CANNOT_MODIFY_IMMUTABLE_ERROR, location)