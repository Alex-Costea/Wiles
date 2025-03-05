package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.STACK_OVERFLOW_ERROR

class StackOverflowException(tokenLocation: TokenLocation) : WilesException(STACK_OVERFLOW_ERROR, tokenLocation)