package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.WilesException

class UnexpectedEndException(s: String, location: TokenLocation) : WilesException(s, location)