package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class CannotCallMethodException(location: TokenLocation,
                                message : String = "Function cannot be called with these arguments!") :
    AbstractCompilationException(message, location)