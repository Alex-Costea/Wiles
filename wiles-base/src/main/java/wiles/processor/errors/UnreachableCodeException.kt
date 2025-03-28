package wiles.processor.errors

import wiles.shared.constants.ErrorMessages.UNREACHABLE_CODE_ERROR
import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class UnreachableCodeException(tokenLocation: TokenLocation) : WilesException(UNREACHABLE_CODE_ERROR, tokenLocation)