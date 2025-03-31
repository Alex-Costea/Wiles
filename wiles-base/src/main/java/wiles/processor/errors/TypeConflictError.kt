package wiles.processor.errors

import wiles.processor.types.WilesType
import wiles.shared.constants.ErrorMessages.TYPE_CONFLICT_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class TypeConflictError(superType : WilesType, subType : WilesType, tokenLocation: TokenLocation)
    : WilesException(TYPE_CONFLICT_ERROR.format(superType, subType), tokenLocation)