package wiles.checker

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Types
import kotlin.Exception

object Utils {
    fun isSubtype(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        TODO("subtypes")
    }

    fun inferTypeFromLiteral(name : String, variables : HashMap<String,VariableDetails>) : JSONStatement
    {
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(Types.STRING_ID, type = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(Types.DOUBLE_ID, type = SyntaxType.TYPE)
            return JSONStatement(Types.INT64_ID, type = SyntaxType.TYPE)
        }
        if(Predicates.IS_IDENTIFIER.test(name)) {
            if(variables[name]?.initialized==false)
                throw Exception("Variable used before being initialized!")
            return variables[name]?.type ?: throw Exception("Unknown variable!")
        }
        throw InternalErrorException("Not one token!")
    }
}