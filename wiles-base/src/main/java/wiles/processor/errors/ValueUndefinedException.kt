package wiles.processor.errors

import wiles.shared.constants.ErrorMessages.VALUE_UNDEFINED_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class ValueUndefinedException(tokenLocation: TokenLocation)
    : WilesException(VALUE_UNDEFINED_ERROR, tokenLocation)