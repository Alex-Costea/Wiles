package wiles.interpreter

import wiles.interpreter.interpreters.InterpretFromCodeBlock
import wiles.interpreter.interpreters.InterpretFromStatement
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InterpreterService(val statement : JSONStatement,
                         val variables : VariableMap,
                         val additionalVars : VariableMap) {
    fun run()
    {
        val interpreter : InterpretFromStatement
        when(statement.type)
        {
            SyntaxType.METHOD -> TODO()
            SyntaxType.EXPRESSION -> TODO()
            SyntaxType.CODE_BLOCK -> interpreter = InterpretFromCodeBlock(statement, variables, additionalVars)
            SyntaxType.TOKEN -> TODO()
            SyntaxType.TYPE -> TODO()
            SyntaxType.IF -> TODO()
            SyntaxType.WHEN -> TODO()
            SyntaxType.DECLARATION -> TODO()
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