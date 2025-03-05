package wiles.parser.exceptions

import wiles.shared.errors.WilesException
import wiles.shared.data.TokenLocation

class StringInvalidException(s: String, tokenLocation: TokenLocation) :
    WilesException(s, tokenLocation)