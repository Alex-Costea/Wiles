package wiles.interpreter.services

import wiles.interpreter.data.VariableMap
import wiles.interpreter.interpreters.InterpretFromCodeBlock
import wiles.interpreter.interpreters.InterpretFromDeclaration
import wiles.interpreter.interpreters.InterpretFromExpression
import wiles.interpreter.interpreters.InterpretFromStatement
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InterpretFrom(val statement : JSONStatement,
                    val variables : VariableMap,
                    val additionalVars : VariableMap
) {
    fun interpret()
    {
        val interpreter : InterpretFromStatement
        when(statement.type)
        {
            SyntaxType.METHOD -> TODO()
            SyntaxType.EXPRESSION -> interpreter = InterpretFromExpression(statement, variables, additionalVars)
            SyntaxType.CODE_BLOCK -> interpreter = InterpretFromCodeBlock(statement, variables, additionalVars)
            SyntaxType.TOKEN -> TODO()
            SyntaxType.TYPE -> TODO()
            SyntaxType.IF -> TODO()
            SyntaxType.WHEN -> TODO()
            SyntaxType.DECLARATION -> interpreter = InterpretFromDeclaration(statement, variables, additionalVars)
            SyntaxType.RETURN -> TODO()
            SyntaxType.WHILE -> TODO()
            SyntaxType.BREAK -> TODO()
            SyntaxType.CONTINUE -> TODO()
            SyntaxType.METHOD_CALL -> TODO()
            SyntaxType.LIST -> TODO()
            SyntaxType.FOR -> TODO()
            null -> throw InternalErrorException()
        }
        interpreter.interpret()
    }
}