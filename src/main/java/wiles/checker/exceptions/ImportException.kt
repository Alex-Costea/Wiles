package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class ImportException(tokenLocation: TokenLocation) :
    AbstractCompilationException("Can only import one identifier!", tokenLocation)