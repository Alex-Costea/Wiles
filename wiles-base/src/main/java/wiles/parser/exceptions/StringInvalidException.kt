package wiles.parser.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class StringInvalidException(s: String, tokenLocation: TokenLocation) :
    AbstractCompilationException(s, tokenLocation)