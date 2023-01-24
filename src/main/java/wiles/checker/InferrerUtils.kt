package wiles.checker

import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.exceptions.UsedBeforeInitializationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Types

object InferrerUtils {
    fun isSubtype(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)
        return false
        //TODO("subtypes")
    }

    fun inferTypeFromLiteral(token : JSONStatement, variables : HashMap<String,VariableDetails>) : JSONStatement
    {
        assert(token.type == SyntaxType.TOKEN)
        val name = token.name
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
                throw UsedBeforeInitializationException(token.location!!)
            return variables[name]?.type ?: throw UnknownIdentifierException(token.location!!)
        }
        throw InternalErrorException("Not one token!")
    }
}