package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class UnusedExpressionException(location : TokenLocation)
    : AbstractCompilationException("The result of this expression is never used!", location)