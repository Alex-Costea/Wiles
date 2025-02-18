package wiles.processor.errors

import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.UNKNOWN_IDENTIFIER_ERROR

class IdentifierUnknownException(tokenLocation: TokenLocation) : WilesException(
    UNKNOWN_IDENTIFIER_ERROR,
    tokenLocation
)