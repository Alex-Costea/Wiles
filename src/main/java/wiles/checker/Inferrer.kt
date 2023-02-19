package wiles.checker

import wiles.checker.inferrers.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*

// Adds inferred type definitions and return types
// Checks correct declarations/initializations
// Adds types to all expressions
// Checks correct types when specified
// Adds error for unknown types (not done in parser!)
class Inferrer(details: InferrerDetails)
{
    val statement = details.statement
    private val variables = details.variables
    val exceptions = details.exceptions

    fun getType(): JSONStatement {
        if(statement.components.getOrNull(0)?.type == TYPE)
            return statement.components[0]
        throw InternalErrorException("Unknown type!")
    }


    fun infer()
    {
        val details = InferrerDetails(statement, variables, exceptions)
        val inferFromStatement : InferFromStatement
        when (statement.type)
        {
            CODE_BLOCK -> inferFromStatement = InferFromCodeBlock(details)
            DECLARATION -> inferFromStatement = InferFromDeclaration(details)
            EXPRESSION -> inferFromStatement = InferFromExpression(details)

            //should be part of expressions
            LIST -> inferFromStatement = InferFromList(details)
            TYPE -> inferFromStatement = InferFromType(details)
            METHOD -> inferFromStatement = InferFromMethod(details)
            METHOD_CALL -> inferFromStatement = InferFromMethodCall(details)
            WHEN_EXPRESSION -> TODO()

            //should not appear at all
            null, TOKEN -> throw InternalErrorException("Null statement type")

            //others
            RETURN -> inferFromStatement = InferFromReturn(details)
            WHEN -> TODO()
            FOR -> TODO()
            WHILE -> TODO()
            BREAK -> TODO()
            CONTINUE -> TODO()
        }
        inferFromStatement.infer()
    }
}