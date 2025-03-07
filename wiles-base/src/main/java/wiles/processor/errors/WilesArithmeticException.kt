package wiles.processor.errors

import wiles.shared.constants.ErrorMessages.ARITHMETIC_EXCEPTION_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class WilesArithmeticException(tokenLocation: TokenLocation)
    : WilesException(ARITHMETIC_EXCEPTION_ERROR, tokenLocation)
