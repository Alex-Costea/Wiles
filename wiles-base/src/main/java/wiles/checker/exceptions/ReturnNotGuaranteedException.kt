package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.RETURN_NOT_GUARANTEED_ERROR

class ReturnNotGuaranteedException(tokenLocation: TokenLocation)
    : AbstractCompilationException(RETURN_NOT_GUARANTEED_ERROR, tokenLocation)