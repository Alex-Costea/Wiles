package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class UnknownTypeException(location: TokenLocation) :
    AbstractCompilationException("Unknown type!", location)