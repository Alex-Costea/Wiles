package wiles.checker

import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Types
import java.lang.Exception

object Utils {
    fun isSubtype(type1 : JSONStatement, type2 : JSONStatement) : Boolean
    {
        //TODO
        return true
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
        if(Predicates.IS_IDENTIFIER.test(name))
            return variables[name]?.type ?: TODO("figure out")
        throw Exception("Unknown type or something")
    }
}