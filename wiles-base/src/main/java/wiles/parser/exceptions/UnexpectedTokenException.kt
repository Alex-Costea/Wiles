package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.WilesException

class UnexpectedTokenException(s: String, where: TokenLocation) :
    WilesException(s, where)