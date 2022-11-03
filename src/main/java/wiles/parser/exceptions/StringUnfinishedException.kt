package wiles.parser.exceptions

import wiles.parser.data.TokenLocation

class StringUnfinishedException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(s, TokenLocation(line, lineIndex))