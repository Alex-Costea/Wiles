package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.AbstractCompilationException

class UnexpectedEndException(s: String, location: TokenLocation) : AbstractCompilationException(s, location)