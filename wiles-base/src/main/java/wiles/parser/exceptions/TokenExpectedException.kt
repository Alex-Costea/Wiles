package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.WilesException

class TokenExpectedException(s: String, tokenLocation: TokenLocation) : WilesException(s, tokenLocation)