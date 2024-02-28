package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.AbstractCompilationException

class TokenExpectedException(s: String, tokenLocation: TokenLocation) : AbstractCompilationException(s, tokenLocation)