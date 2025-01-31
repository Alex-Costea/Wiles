package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.checker.exceptions.VariableAlreadyDeclaredException
import wiles.checker.services.InferrerService
import wiles.shared.AbstractCompilationException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.constants.TypeConstants.ERROR_TYPE
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InferFromDeclaration(
    details: InferrerDetails,
    private val inFunction: Boolean = false
)
    : InferFromStatement(details)
{
    override fun infer() {
        //get details
        assert(statement.components.size>1)
        val name = if(statement.components[0].syntaxType== TYPE) statement.components[1] else statement.components[0]
        val type = if(statement.components[0].syntaxType== TYPE) statement.components[0] else null
        val default = statement.components.getOrNull(if(type==null) 1 else 2)
        var inferredType : JSONStatement? = null

        if(variables.containsKey(name.name))
            throw VariableAlreadyDeclaredException(name.getFirstLocation())

        try
        {
            if (default != null) {
                val inferrer = InferrerService(InferrerDetails(default, variables, exceptions))
                inferrer.infer()
                inferredType = inferrer.getType()
                if(inFunction)
                    inferredType = inferredType.copy()
            }
        }
        catch (ex : AbstractCompilationException)
        {
            variables[name.name] = VariableDetails(ERROR_TYPE,true)
            throw ex
        }

        val newType = type?:inferredType!!

        val isNothing =  (if(type!=null) isFormerSuperTypeOfLatter(NOTHING_TYPE, type) else false)

        if(isNothing)
            statement.components.add(
                JSONStatement(
                    syntaxType = EXPRESSION,
                    components = mutableListOf(JSONStatement(
                        name = NOTHING_ID,
                        syntaxType = TOKEN
                    ))
                ))

        //type nothing is auto-initialized with nothing
        variables[name.name] = VariableDetails(newType,
            initialized = (default != null || isNothing) && !inFunction,
            modifiable = statement.name.contains(VARIABLE_ID)
        )

        if(statement.components[0].syntaxType != TYPE)
            statement.components.add(0,newType)

        if(type != null)
        {
            InferFromType(InferrerDetails(type, variables, exceptions)).infer()
            if(inferredType!=null && !isFormerSuperTypeOfLatter(type, inferredType))
                throw ConflictingTypeDefinitionException(type.getFirstLocation(),
                    type.toString(),inferredType.toString())
        }
        else
        {
            // if default value is literal nothing, there's not enough information
            if(isFormerSuperTypeOfLatter(NOTHING_TYPE, inferredType!!))
                throw InferenceFailException(statement.getFirstLocation())
        }
    }
}