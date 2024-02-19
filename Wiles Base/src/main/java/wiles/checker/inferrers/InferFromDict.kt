package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.OBJECT_ID
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types.DICT_ID

class InferFromDict(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        assert(statement.syntaxType==SyntaxType.DICT)
        if(statement.name != OBJECT_ID)
        {
            val dictStatedType = if(statement.components.isNotEmpty() && statement.components[0].syntaxType ==
                SyntaxType.TYPE) statement.components[0] else null
            val inferredTypes : MutableList<JSONStatement?> = mutableListOf(null,null)
            var index = 0
            for(component in statement.components)
            {
                var inferredType = inferredTypes[index]
                if(component.syntaxType==SyntaxType.TYPE)
                    continue
                assert(component.syntaxType==SyntaxType.EXPRESSION)
                val inferrer = InferFromExpression(
                    InferrerDetails(component, variables, CompilationExceptionsCollection(), additionalVars, context)
                )
                inferrer.infer()
                val newType = component.components[0]
                exceptions.addAll(inferrer.exceptions)

                inferredType = if(inferredType==null || isFormerSuperTypeOfLatter(newType,inferredType))
                    newType
                else if(isFormerSuperTypeOfLatter(inferredType,newType))
                    inferredType
                else{
                    val statedType = dictStatedType?.components?.get(index)
                    if(statedType!=null)
                    {
                        if(isFormerSuperTypeOfLatter(statedType,newType)) {
                            statedType
                        }
                        else throw ConflictingTypeDefinitionException(component.getFirstLocation(),
                            statedType.toString(),newType.toString())
                    }
                    else throw InferenceFailException(component.getFirstLocation())
                }
                inferredTypes[index] = inferredType
                index = (index + 1) % 2
            }

            var finalTypes : MutableList<JSONStatement> = mutableListOf()
            for(newIndex in 0 .. 1) {
                val inferredType = inferredTypes[newIndex]
                val statedType = dictStatedType?.getComponents()?.get(newIndex)
                if (statedType != null) {
                    if (inferredType != null) {
                        if (!isFormerSuperTypeOfLatter(statedType, inferredType))
                            throw ConflictingTypeDefinitionException(
                                statement.components[0].getFirstLocation(),
                                statedType.toString(), inferredType.toString()
                            )
                        else finalTypes.add(statedType)
                    }
                } else if (inferredType != null) finalTypes.add(inferredType)
                else throw InferenceFailException(statement.getFirstLocation())
            }
            if(statement.components.first().syntaxType == SyntaxType.TYPE)
                statement.components.removeFirst()
            if(finalTypes.size == 0)
                finalTypes = dictStatedType?.getComponents()!!
            statement.components.add(0, JSONStatement(syntaxType = SyntaxType.TYPE, name = DICT_ID,
                components = finalTypes.map { it }.toMutableList()))
        }
        else TODO()
    }
}