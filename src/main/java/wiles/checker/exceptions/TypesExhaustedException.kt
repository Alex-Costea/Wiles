package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class TypesExhaustedException(tokenLocation: TokenLocation)
    : AbstractCompilationException("There is no type possible here.", tokenLocation)