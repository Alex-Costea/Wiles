package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.CONFLICTING_TYPES_ERROR

class ConflictingTypeDefinitionException(location: TokenLocation, type1 : String, type2 : String)
    : AbstractCompilationException(CONFLICTING_TYPES_ERROR.format(type1, type2),location)