package wiles.parser.exceptions

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException

class UnexpectedTokenException(s: String, where: TokenLocation) :
    WilesException(s, where)