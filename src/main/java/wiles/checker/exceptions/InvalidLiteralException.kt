package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class InvalidLiteralException(tokenLocation: TokenLocation)
    : AbstractCompilationException("This literal is invalid.", tokenLocation)