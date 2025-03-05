package wiles.parser.exceptions

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class TokenExpectedException(s: String, tokenLocation: TokenLocation) : WilesException(s, tokenLocation)