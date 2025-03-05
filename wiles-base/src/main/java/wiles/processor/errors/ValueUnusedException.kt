package wiles.processor.errors

import wiles.shared.constants.ErrorMessages.VALUE_UNUSED_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class ValueUnusedException(tokenLocation: TokenLocation)
    : WilesException(VALUE_UNUSED_ERROR, tokenLocation, )