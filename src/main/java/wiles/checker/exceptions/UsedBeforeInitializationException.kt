package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class UsedBeforeInitializationException(location: TokenLocation) :
    AbstractCompilationException("Variable used before being initialized!", location)