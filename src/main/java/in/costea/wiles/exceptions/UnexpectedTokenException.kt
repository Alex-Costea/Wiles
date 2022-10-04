package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class UnexpectedTokenException(s: String, where: TokenLocation?) : AbstractCompilationException("Unexpected token: $s", where)