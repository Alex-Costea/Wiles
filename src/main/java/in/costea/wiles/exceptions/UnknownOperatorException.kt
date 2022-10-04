package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class UnknownOperatorException(s: String, line: Int, lineIndex: Int) : AbstractCompilationException("Operator unknown: $s", TokenLocation(line, lineIndex))