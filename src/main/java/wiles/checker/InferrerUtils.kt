package wiles.checker

import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.exceptions.UnknownTypeException
import wiles.checker.exceptions.UsedBeforeInitializationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.STRING_ID

object InferrerUtils {
    fun isFormerSuperTypeOfLatter(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)
        if(supertype.toString() == subtype.toString())
            return true

        if(supertype.name == ANYTHING_ID)
        {
            if(subtype.name != EITHER_ID)
            {
                if (isFormerSuperTypeOfLatter(NOTHING_TYPE, subtype))
                    return false
                return true
            }
        }

        else if(supertype.name == EITHER_ID)
        {
            if(subtype.name != EITHER_ID)
            {
                for (component in supertype.components)
                {
                    if (isFormerSuperTypeOfLatter(component,subtype))
                    {
                        return true
                    }
                }
            }
            else
            {
                for(subtypeComponent in subtype.components)
                {
                    if(isFormerSuperTypeOfLatter(supertype,subtypeComponent))
                    {
                        continue
                    }
                    var hasMatch = false
                    for(supertypeComponent in supertype.components)
                    {
                        if(isFormerSuperTypeOfLatter(supertypeComponent,subtypeComponent))
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
                if(!isFormerSuperTypeOfLatter(supertype, component)) {
                    return false
                }
            }
            return true
        }

        else if (supertype.name == LIST_ID && subtype.name == LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0])

        //TODO: not complete!

        return false
    }

    fun inferTypeFromLiteral(token : JSONStatement, variables : HashMap<String,VariableDetails>) : JSONStatement
    {
        assert(token.type == SyntaxType.TOKEN)
        val name = token.name
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(STRING_ID, type = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(DOUBLE_ID, type = SyntaxType.TYPE)
            return JSONStatement(INT64_ID, type = SyntaxType.TYPE)
        }
        if(IS_IDENTIFIER.test(name)) {
            if( variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.location!!)
            return JSONStatement(
                name = variables[name]?.type?.name ?: throw UnknownIdentifierException(token.location!!),
                type = SyntaxType.TYPE,
                components = variables[name]!!.type.components.map { it.copyRemovingLocation() }.toMutableList())
        }
        throw InternalErrorException("Not one token!")
    }

    fun checkTypeIsDefined(type : JSONStatement)
    {
        if(type.type == SyntaxType.TYPE && type.name.startsWith("!"))
            throw UnknownTypeException(type.getFirstLocation())
        if(type.components.isNotEmpty())
        {
            for(component in type.components)
            {
                checkTypeIsDefined(component)
            }
        }
    }

    fun makeNullable(type: JSONStatement) : JSONStatement
    {
        return JSONStatement(name = EITHER_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation(), NOTHING_TYPE))
    }
}