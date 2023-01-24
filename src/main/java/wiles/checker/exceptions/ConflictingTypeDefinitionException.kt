package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class ConflictingTypeDefinitionException(location: TokenLocation)
    : AbstractCompilationException("Type definition is in conflict with inferred type!",location)