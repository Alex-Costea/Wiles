package wiles.parser.exceptions

import wiles.parser.data.TokenLocation

class TokenExpectedException(s: String, tokenLocation: TokenLocation) : AbstractCompilationException(s, tokenLocation)