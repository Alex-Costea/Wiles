package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.RECURSIVE_TYPE_DEFINITION_ERROR

class RecursiveTypeDefinitionException(tokenLocation: TokenLocation)
    : AbstractCompilationException(RECURSIVE_TYPE_DEFINITION_ERROR, tokenLocation)