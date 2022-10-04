package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class TokenExpectedException(s: String, tokenLocation: TokenLocation?) : AbstractCompilationException(s, tokenLocation)