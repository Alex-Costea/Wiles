package wiles.processor.errors

import wiles.processor.types.AbstractType
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.TYPE_CONFLICT_ERROR

class TypeConflictError(superType : AbstractType, subType : AbstractType, tokenLocation: TokenLocation)
    : WilesException(TYPE_CONFLICT_ERROR.format(superType, subType), tokenLocation)