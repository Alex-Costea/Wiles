package wiles.checker

import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.exceptions.UsedBeforeInitializationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.BOOLEAN_LITERALS
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Types
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID

object InferrerUtils {
    //TODO: not complete!
    fun isSubtype(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)
        if(supertype.toString() == subtype.toString())
            return true

        if(supertype.name == ANYTHING_ID && subtype.name != EITHER_ID)
        {
            if(subtype.name == NOTHING_ID)
                return false
            return true
        }

        if(supertype.name == EITHER_ID)
        {
            if(subtype.name != EITHER_ID)
            {
                for (component in supertype.components)
                {
                    if (isSubtype(component,subtype))
                    {
                        return true
                    }
                }
            }
            else
            {
                for(subtypeComponent in subtype.components)
                {
                    if(isSubtype(supertype,subtypeComponent))
                    {
                        continue
                    }
                    var hasMatch = false
                    for(supertypeComponent in supertype.components)
                    {
                        if(isSubtype(supertypeComponent,subtypeComponent))
                        {
                            hasMatch = true
                            break
                        }
                    }
                    if(!hasMatch)
                        return false
                }
                return true
            }
        }
        else if(subtype.name == EITHER_ID)
        {
            for(component in subtype.components)
            {
                if(!isSubtype(supertype, component)) {
                    return false
                }
            }
            return true
        }

        return false
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
        if(BOOLEAN_LITERALS.contains(name))
            return JSONStatement(Types.BOOLEAN_ID, type = SyntaxType.TYPE)
        if(name == NOTHING_ID)
            return JSONStatement(NOTHING_ID, type = SyntaxType.TYPE)
        if(Predicates.IS_IDENTIFIER.test(name)) {
            if(variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.location!!)
            return variables[name]?.type ?: throw UnknownIdentifierException(token.location!!)
        }
        throw InternalErrorException("Not one token!")
    }
}