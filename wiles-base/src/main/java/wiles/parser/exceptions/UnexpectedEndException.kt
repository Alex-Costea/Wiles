package wiles.parser.exceptions

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class UnexpectedEndException(s: String, location: TokenLocation) : WilesException(s, location)