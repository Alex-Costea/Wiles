package wiles.parser.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class StringInvalidException(s: String, line: Int, lineIndex: Int) :
    AbstractCompilationException(s, TokenLocation(line, lineIndex))