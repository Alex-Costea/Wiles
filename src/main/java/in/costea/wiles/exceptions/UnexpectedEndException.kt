package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class UnexpectedEndException(s: String, location: TokenLocation) : AbstractCompilationException(s, location)