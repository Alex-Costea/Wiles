package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class WrongOperationException(location: TokenLocation, type1 : String, type2 : String) : AbstractCompilationException(
    "Operation cannot be executed between types $type1 and $type2.", location)