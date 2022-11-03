package wiles.parser.exceptions

import wiles.parser.data.TokenLocation

class UnexpectedTokenException(s: String, where: TokenLocation) :
    AbstractCompilationException(s, where)