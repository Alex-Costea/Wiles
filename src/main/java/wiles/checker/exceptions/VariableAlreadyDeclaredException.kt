package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class VariableAlreadyDeclaredException(location: TokenLocation)
    : AbstractCompilationException("Variable already declared!",location)