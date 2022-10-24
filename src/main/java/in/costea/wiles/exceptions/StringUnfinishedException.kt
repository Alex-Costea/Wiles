package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class StringUnfinishedException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(s, TokenLocation(line, lineIndex))