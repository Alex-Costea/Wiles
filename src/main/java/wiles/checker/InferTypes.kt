package wiles.checker

import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*

//TODO
// Add inferred type definitions and return types
// Check correct declarations/initializations
// Add types to operations
// Check correct types when specified
class InferTypes(private val statement : JSONStatement, private val variables : HashMap<String,VariableDetails>)
{

    private fun inferFromCodeBlock()
    {
        for(part in statement.components)
        {
            InferTypes(part,variables).infer()
        }
    }

    private fun inferFromDeclaration()
    {
        //get details
        assert(statement.components.size>1)
        val name = if(statement.components[0].type==TYPE) statement.components[1] else statement.components[0]
        var type = if(statement.components[0].type==TYPE) statement.components[0] else null
        val default =statement.components.getOrNull(if(type==null) 1 else 2)

        if(variables.containsKey(name.name))
            throw Exception("Variable already declared!")

        if(type==null)
        {
            val inferrer = InferTypes(default!!,variables)
            inferrer.infer()
            type = inferrer.getType()
        }

        variables[name.name] = VariableDetails(type,default != null)
    }

    private fun getType(): JSONStatement {
        if(statement.components.getOrNull(0)?.type == TYPE)
            return statement.components[0]
        throw Exception("Idk!")
    }


    private fun inferFromExpression()
    {
        if(statement.components.size==1)
        {
            val type = Utils.inferTypeFromLiteral(statement.components[0].name,variables)
            statement.components.add(0,type)
        }
        else TODO()
    }

    fun infer()
    {
        when(statement.type)
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
            null -> TODO()
        }
    }
}