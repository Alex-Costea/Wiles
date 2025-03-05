package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.EXPRESSION_CANT_BE_MODIFIED_ERROR

class CantBeModifiedException(tokenLocation: TokenLocation) : WilesException(EXPRESSION_CANT_BE_MODIFIED_ERROR,
    tokenLocation
)