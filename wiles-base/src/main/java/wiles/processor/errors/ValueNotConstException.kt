package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.VALUE_NOT_CONST_ERROR

class ValueNotConstException(tokenLocation: TokenLocation) : WilesException(VALUE_NOT_CONST_ERROR, tokenLocation)