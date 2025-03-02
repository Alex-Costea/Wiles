package wiles.processor.errors

import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.VALUE_NOT_CONST_ERROR

class ValueNotConstException(tokenLocation: TokenLocation) : WilesException(VALUE_NOT_CONST_ERROR, tokenLocation)