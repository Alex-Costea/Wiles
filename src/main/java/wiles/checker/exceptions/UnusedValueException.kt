package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class UnusedValueException(location : TokenLocation)
    : AbstractCompilationException("Unused value!", location)