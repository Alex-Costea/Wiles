package wiles.checker

import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.VariableAlreadyDeclaredException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*

//TODO
// Add inferred type definitions and return types
// Check correct declarations/initializations
// Add types to all expressions
// Check correct types when specified
// Add error for unknown types (not done in parser!)
class Inferrer(private val statement : JSONStatement, private val variables : HashMap<String,VariableDetails>)
{
    private val errorType = JSONStatement(name = "ERROR")
    val exceptions = CompilationExceptionsCollection()
    private fun inferFromCodeBlock()
    {
        for(part in statement.components)
        {
            try
            {
                Inferrer(part, variables).infer()
            }
            catch (ex : AbstractCompilationException)
            {
                exceptions.add(ex)
            }
        }
    }

    private fun inferFromDeclaration()
    {
        //get details
        assert(statement.components.size>1)
        val name = if(statement.components[0].type==TYPE) statement.components[1] else statement.components[0]
        val type = if(statement.components[0].type==TYPE) statement.components[0] else null
        val default = statement.components.getOrNull(if(type==null) 1 else 2)
        var inferredType : JSONStatement? = null

        if(variables.containsKey(name.name))
            throw VariableAlreadyDeclaredException(name.location!!)

        try
        {
            if (default != null) {
                val inferrer = Inferrer(default, variables)
                inferrer.infer()
                inferredType = inferrer.getType()
            }
        }
        catch (ex : AbstractCompilationException)
        {
            variables[name.name] = VariableDetails(errorType,true)
            throw ex
        }

        variables[name.name] = VariableDetails(type?:inferredType!!,default != null)

        if(type != null)
        {
            if(inferredType!=null && !InferrerUtils.isSubtype(inferredType,type))
                throw ConflictingTypeDefinitionException(type.location!!)
        }
    }

    private fun getType(): JSONStatement {
        if(statement.components.getOrNull(0)?.type == TYPE)
            return statement.components[0]
        throw InternalErrorException("Unknown type!")
    }


    private fun inferFromExpression()
    {
        if(statement.components.size==1 && statement.components[0].type == TOKEN)
        {
            val type = InferrerUtils.inferTypeFromLiteral(statement.components[0],variables)
            statement.components.add(0,type)
        }
        else TODO()
    }

    fun infer()
    {
        when (statement.type)
        {
            CODE_BLOCK -> inferFromCodeBlock()
            DECLARATION -> inferFromDeclaration()
            EXPRESSION -> inferFromExpression()
            LIST -> TODO()
            METHOD -> TODO()

            //others
            TOKEN -> TODO()
            TYPE -> TODO()
            WHEN -> TODO()
            RETURN -> TODO()
            WHILE -> TODO()
            BREAK -> TODO()
            CONTINUE -> TODO()
            METHOD_CALL -> TODO()
            FOR -> TODO()
            null -> throw InternalErrorException("Unknown statement")
        }
    }
}