package wiles.checker

import wiles.checker.exceptions.InvalidOperationException
import wiles.shared.TokenLocation
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.UNARY_MINUS_ID
import wiles.shared.constants.Tokens.UNARY_PLUS_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID

class OperationInferrer(private val location: TokenLocation) {
    private val resultsIn = HashMap<Triple<String?, String, String>, OperationType>()

    init {
        resultsIn[Triple(null, UNARY_PLUS_ID,INT64_ID)] =
            OperationType("PLUS_INT64", TypeDefinition(INT64_ID))
        resultsIn[Triple(null, UNARY_MINUS_ID,INT64_ID)] =
            OperationType("MINUS_INT64", TypeDefinition(INT64_ID))
        resultsIn[Triple(INT64_ID, PLUS_ID,INT64_ID)] =
            OperationType("INT64_PLUS_INT64", TypeDefinition(INT64_ID))
        resultsIn[Triple(INT64_ID, MINUS_ID,INT64_ID)] =
            OperationType("INT64_MINUS_INT64", TypeDefinition(INT64_ID))
        resultsIn[Triple(STRING_ID, PLUS_ID,INT64_ID)] =
            OperationType("STRING_PLUS_INT64", TypeDefinition(STRING_ID))
        resultsIn[Triple(INT64_ID, PLUS_ID, STRING_ID)] =
            OperationType("INT64_PLUS_INT64", TypeDefinition(STRING_ID))
        resultsIn[Triple(STRING_ID, PLUS_ID, STRING_ID)] =
            OperationType("STRING_PLUS_STRING", TypeDefinition(STRING_ID))
    }

    fun resultsType(left: TypeDefinition?, operation: String, right: TypeDefinition): TypeDefinition {
        return resultsIn[Triple(left?.name, operation, right.name)]?.type
            ?: throw InvalidOperationException(location)
    }
}