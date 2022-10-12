package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.TokenLocation

class UnknownTokenException(s: String, line: Int, lineIndex: Int) : AbstractCompilationException("Token unknown: $s", TokenLocation(line, lineIndex))