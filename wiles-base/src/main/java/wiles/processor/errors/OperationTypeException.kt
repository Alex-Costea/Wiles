package wiles.processor.errors

import wiles.processor.types.AbstractType
import wiles.shared.constants.ErrorMessages.OPERATION_TYPE_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class OperationTypeException(type1 : AbstractType?, type2 : AbstractType, tokenLocation: TokenLocation)
    : WilesException(OPERATION_TYPE_ERROR.format(
    if(type1 != null) "types $type1 and $type2" else "type $type2"), tokenLocation)