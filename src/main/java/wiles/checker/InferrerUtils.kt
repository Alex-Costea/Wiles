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
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
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

        else if (subtype.name == MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0])

        else if(supertype.name == METHOD_ID && subtype.name == METHOD_ID)
        {
            val supertypeComponents = supertype.components[0].components.toMutableList()
            val subtypeComponents = subtype.components[0].components.toMutableList()

            val supertypeReturnType = supertypeComponents.filter { it.type == SyntaxType.TYPE }
                .getOrNull(0) ?: NOTHING_TYPE

            val subtypeReturnType = subtypeComponents.filter { it.type == SyntaxType.TYPE }
                .getOrNull(0) ?: NOTHING_TYPE

            if(!isFormerSuperTypeOfLatter(supertypeReturnType,subtypeReturnType))
                return false

            //TODO: better method matching, with subtyping
            if(matchMethodComponentList(subtypeComponents,supertypeComponents) &&
                    matchMethodComponentList(supertypeComponents,subtypeComponents))
            {
                //check unnamed args are in same order
                while(supertypeComponents.isNotEmpty() && subtypeComponents.isNotEmpty())
                {
                    val elem1 = supertypeComponents[0]
                    if(elem1.type == SyntaxType.TYPE || elem1.name!= ANON_ARG_ID) {
                        supertypeComponents.removeFirst()
                        continue
                    }

                    val elem2 = subtypeComponents[0]
                    if(elem2.type == SyntaxType.TYPE || elem2.name!= ANON_ARG_ID) {
                        subtypeComponents.removeFirst()
                        continue
                    }

                    if(elem1.toString()!=elem2.toString())
                        return false

                    supertypeComponents.removeFirst()
                    subtypeComponents.removeFirst()
                }
                if(supertypeComponents.isNotEmpty() || subtypeComponents.isNotEmpty())
                    return false
                return true
            }
        }

        return false
    }

    private fun matchMethodComponentList(list1 : List<JSONStatement>, list2 : List<JSONStatement>) : Boolean
    {
        for (component1 in list1) {
            if (component1.type == SyntaxType.TYPE)
                continue
            var matchFound = false
            for (component2 in list2) {
                if (component2.type == SyntaxType.TYPE)
                    continue
                if (component2.toString() == component1.toString())
                    matchFound = true
            }
            if (!matchFound)
                return false
        }
        return true
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

    fun makeMutable(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = MUTABLE_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeList(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = LIST_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }
}