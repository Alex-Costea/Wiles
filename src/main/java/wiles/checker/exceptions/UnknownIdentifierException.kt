package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class UnknownIdentifierException(location: TokenLocation) :
    AbstractCompilationException("Unknown identifier!", location)