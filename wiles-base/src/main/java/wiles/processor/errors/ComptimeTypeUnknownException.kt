package wiles.processor.errors

import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.COMPTIME_TYPE_UNKNOWN_ERROR

class ComptimeTypeUnknownException(tokenLocation: TokenLocation)
    : WilesException(COMPTIME_TYPE_UNKNOWN_ERROR, tokenLocation)