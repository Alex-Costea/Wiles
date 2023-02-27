package wiles.checker.services

import wiles.checker.data.InferrerDetails
import wiles.checker.inferrers.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*

// Adds inferred type definitions and return types
// Checks correct declarations/initializations
// Adds types to all expressions
// Checks correct types when specified
// Adds error for unknown types (not done in parser!)
class InferrerService(details: InferrerDetails)
{
    val statement = details.statement
    private val variables = details.variables
    val exceptions = details.exceptions
    private val additionalVariables = details.additionalVars

    fun getType(): JSONStatement {
        if(statement.components.getOrNull(0)?.type == TYPE)
            return statement.components[0]
        throw InternalErrorException("Unknown type!")
    }


    fun infer()
    {
        val details = InferrerDetails(statement, variables, exceptions, additionalVariables)
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

            //others
            RETURN -> inferFromStatement = InferFromReturn(details)
            WHEN -> throw InternalErrorException("TODO")
            WITH -> inferFromStatement = InferFromWith(details)
            FOR -> inferFromStatement = InferFromFor(details)
            WHILE -> inferFromStatement = InferFromWhile(details)

            //should not appear at all
            null, TOKEN -> throw InternalErrorException("Invalid statement type")

            //nothing to infer
            BREAK, CONTINUE -> return
        }
        inferFromStatement.infer()
    }
}