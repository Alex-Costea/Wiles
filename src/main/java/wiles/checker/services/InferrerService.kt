package wiles.checker.services

import wiles.checker.data.InferrerDetails
import wiles.checker.inferrers.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType.*
import wiles.shared.constants.ErrorMessages.IRREGULAR_STATEMENT_ERROR
import wiles.shared.constants.ErrorMessages.UNKNOWN_SYNTAX_TYPE_ERROR

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
        if(statement.components.getOrNull(0)?.syntaxType == TYPE)
            return statement.components[0]
        throw InternalErrorException(UNKNOWN_SYNTAX_TYPE_ERROR)
    }


    fun infer()
    {
        val details = InferrerDetails(statement, variables, exceptions, additionalVariables)
        val inferFromStatement : InferFromStatement
        when (statement.syntaxType)
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
            IF -> inferFromStatement = InferFromIf(details)
            WHEN -> inferFromStatement = InferFromWhen(details)
            FOR -> inferFromStatement = InferFromFor(details)
            WHILE -> inferFromStatement = InferFromWhile(details)
            TYPEDEF -> inferFromStatement = InferFromTypeDef(details)

            //should not appear at all
            null  -> throw InternalErrorException(IRREGULAR_STATEMENT_ERROR)

            //nothing to infer
            BREAK, TOKEN, CONTINUE -> return
        }
        inferFromStatement.infer()
    }
}