package wiles.checker

import wiles.checker.inferrers.InferFromCodeBlock
import wiles.checker.inferrers.InferFromDeclaration
import wiles.checker.inferrers.InferFromExpression
import wiles.checker.inferrers.InferFromStatement
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*

//TODO
// Add inferred type definitions and return types
// Check correct declarations/initializations
// Add types to all expressions
// Check correct types when specified
// Add error for unknown types (not done in parser!)
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
            LIST -> TODO()
            METHOD -> TODO()
            METHOD_CALL -> TODO()
            TOKEN -> TODO()
            WHEN_EXPRESSION -> TODO()

            //should not appear at all
            TYPE, null -> throw InternalErrorException("Unknown statement")

            //others
            WHEN -> TODO()
            FOR -> TODO()
            WHILE -> TODO()
            BREAK -> TODO()
            CONTINUE -> TODO()
            RETURN -> TODO()
        }
        inferFromStatement.infer()
    }
}