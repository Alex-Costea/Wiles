package wiles.parser.exceptions

import wiles.shared.WilesException
import wiles.shared.TokenLocation

class StringInvalidException(s: String, tokenLocation: TokenLocation) :
    WilesException(s, tokenLocation)