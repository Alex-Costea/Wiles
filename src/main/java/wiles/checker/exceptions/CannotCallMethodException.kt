package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class CannotCallMethodException(location: TokenLocation) :
    AbstractCompilationException("Function cannot be called with these arguments!", location)