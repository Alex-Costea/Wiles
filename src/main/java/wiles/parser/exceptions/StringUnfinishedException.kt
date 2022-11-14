package wiles.parser.exceptions

import wiles.shared.TokenLocation
import wiles.shared.AbstractCompilationException

class StringUnfinishedException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(s, TokenLocation(line, lineIndex))