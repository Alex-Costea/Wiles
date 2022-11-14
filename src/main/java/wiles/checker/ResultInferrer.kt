package wiles.checker

import wiles.checker.exceptions.InvalidOperationException
import wiles.shared.TokenLocation
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.UNARY_MINUS_ID
import wiles.shared.constants.Tokens.UNARY_PLUS_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID

class ResultInferrer(private val location: TokenLocation) {
    private val resultsIn = HashMap<Triple<String?, String, String>, String>()

    init {
        resultsIn[Triple(null, UNARY_PLUS_ID,INT64_ID)] = INT64_ID
        resultsIn[Triple(null, UNARY_MINUS_ID,INT64_ID)] = INT64_ID
        resultsIn[Triple(INT64_ID, PLUS_ID,INT64_ID)] = INT64_ID
        resultsIn[Triple(INT64_ID, MINUS_ID,INT64_ID)] = INT64_ID
        resultsIn[Triple(STRING_ID, PLUS_ID,INT64_ID)] = STRING_ID
        resultsIn[Triple(INT64_ID, PLUS_ID, STRING_ID)] = STRING_ID
        resultsIn[Triple(STRING_ID, PLUS_ID, STRING_ID)] = STRING_ID
    }

    fun results(left: String?, operation: String, right: String): String {
        return resultsIn[Triple(left, operation, right)] ?: throw InvalidOperationException(location)
    }
}