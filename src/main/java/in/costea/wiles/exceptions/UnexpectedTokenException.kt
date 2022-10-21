package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.statics.Constants.UNEXPECTED_TOKEN_ERROR

class UnexpectedTokenException(s: String, where: TokenLocation?) :
    AbstractCompilationException(UNEXPECTED_TOKEN_ERROR.format(s), where)