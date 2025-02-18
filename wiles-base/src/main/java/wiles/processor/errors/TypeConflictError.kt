package wiles.processor.errors

import wiles.processor.types.AbstractType
import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.TYPE_CONFLICT_ERROR

class TypeConflictError(superType : AbstractType, subType : AbstractType, tokenLocation: TokenLocation)
    : WilesException(TYPE_CONFLICT_ERROR.format(superType, subType), tokenLocation)