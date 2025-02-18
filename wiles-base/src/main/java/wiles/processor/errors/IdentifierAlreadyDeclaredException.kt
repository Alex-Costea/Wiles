package wiles.processor.errors

import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.IDENTIFIER_ALREADY_DECLARED_ERROR

class IdentifierAlreadyDeclaredException(tokenLocation: TokenLocation) : WilesException(
    IDENTIFIER_ALREADY_DECLARED_ERROR,
    tokenLocation
)