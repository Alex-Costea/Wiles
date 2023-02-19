package wiles.checker.inferrers

import wiles.checker.CheckerConstants.ERROR_TYPE
import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.Inferrer
import wiles.checker.InferrerDetails
import wiles.checker.InferrerUtils.checkTypeIsDefined
import wiles.checker.InferrerUtils.isFormerSuperTypeOfLatter
import wiles.checker.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.checker.exceptions.VariableAlreadyDeclaredException
import wiles.shared.AbstractCompilationException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.TYPE
import wiles.shared.constants.Tokens

class InferFromDeclaration(details: InferrerDetails,
                           private val alwaysInit: Boolean = false)
    : InferFromStatement(details)
{
    override fun infer() {
        //get details
        assert(statement.components.size>1)
        val name = if(statement.components[0].type== TYPE) statement.components[1] else statement.components[0]
        val type = if(statement.components[0].type== TYPE) statement.components[0] else null
        val default = statement.components.getOrNull(if(type==null) 1 else 2)
        var inferredType : JSONStatement? = null

        if(type!=null)
            checkTypeIsDefined(type)

        if(variables.containsKey(name.name))
            throw VariableAlreadyDeclaredException(name.location!!)

        try
        {
            if (default != null) {
                val inferrer = Inferrer(InferrerDetails(default,variables, exceptions))
                inferrer.infer()
                inferredType = inferrer.getType()
            }
        }
        catch (ex : AbstractCompilationException)
        {
            variables[name.name] = VariableDetails(ERROR_TYPE,true)
            throw ex
        }

        val newType = type?:inferredType!!

        //type nothing is auto-initialized with nothing
        variables[name.name] = VariableDetails(newType,
            initialized = alwaysInit || default != null || (if(type!=null) isFormerSuperTypeOfLatter(NOTHING_TYPE, type) else false),
            modifiable = statement.name == Tokens.VARIABLE_ID
        )

        if(statement.components[0].type != TYPE)
            statement.components.add(0,newType)

        if(type != null)
        {
            if(inferredType!=null && !isFormerSuperTypeOfLatter(type, inferredType))
                throw ConflictingTypeDefinitionException(type.location!!,type.toString(),inferredType.toString())
        }
        else
        {
            // if default value is literal nothing, there's not enough information
            if(isFormerSuperTypeOfLatter(NOTHING_TYPE, inferredType!!))
                throw InferenceFailException(statement.getFirstLocation())
        }
    }
}