package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class ConflictingTypeDefinitionException(location: TokenLocation, type1 : String, type2 : String)
    : AbstractCompilationException("Type definition $type1 is in conflict with inferred type $type2!",location)