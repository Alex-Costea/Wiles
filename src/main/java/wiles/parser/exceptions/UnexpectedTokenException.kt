package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.AbstractCompilationException

class UnexpectedTokenException(s: String, where: TokenLocation) :
    AbstractCompilationException(s, where)