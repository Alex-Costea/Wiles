package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.statics.Constants.TOKEN_UNKNOWN_ERROR

class UnknownTokenException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(TOKEN_UNKNOWN_ERROR.format(s), TokenLocation(line, lineIndex))