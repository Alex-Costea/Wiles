package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.statics.Constants.STRING_UNFINISHED_ERROR

class StringUnfinishedException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(STRING_UNFINISHED_ERROR.format(s), TokenLocation(line, lineIndex))