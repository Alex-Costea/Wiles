package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.IDENTIFIER_ALREADY_DECLARED_ERROR

class IdentifierAlreadyDeclaredException(tokenLocation: TokenLocation) : WilesException(
    IDENTIFIER_ALREADY_DECLARED_ERROR,
    tokenLocation
)