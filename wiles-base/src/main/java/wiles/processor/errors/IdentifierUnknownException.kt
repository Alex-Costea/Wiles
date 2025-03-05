package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.UNKNOWN_IDENTIFIER_ERROR

class IdentifierUnknownException(tokenLocation: TokenLocation) : WilesException(
    UNKNOWN_IDENTIFIER_ERROR,
    tokenLocation
)