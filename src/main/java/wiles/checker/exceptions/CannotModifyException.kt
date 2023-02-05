package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class CannotModifyException(location: TokenLocation) :
    AbstractCompilationException("Cannot modify immutable value!", location)