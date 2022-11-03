package wiles.parser.exceptions

import wiles.parser.data.TokenLocation

class UnexpectedEndException(s: String, location: TokenLocation) : AbstractCompilationException(s, location)