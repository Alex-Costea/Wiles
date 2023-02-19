package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class ReturnNotGuaranteedException(tokenLocation: TokenLocation)
    : AbstractCompilationException("Function is not guaranteed to yield a value", tokenLocation)