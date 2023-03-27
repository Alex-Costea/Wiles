package wiles.interpreter.services

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.interpreters.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InterpreterService(val statement : JSONStatement,
                         val variables : InterpreterVariableMap,
                         val additionalVars : InterpreterVariableMap
) {
    fun interpret()
    {
        val interpreter = when(statement.syntaxType) {
            SyntaxType.EXPRESSION -> InterpretFromExpression(statement, variables, additionalVars)
            SyntaxType.CODE_BLOCK -> InterpretFromCodeBlock(statement, variables, additionalVars)
            SyntaxType.IF -> InterpretFromIf(statement, variables, additionalVars)
            SyntaxType.WHEN -> InterpretFromWhen(statement, variables, additionalVars)
            SyntaxType.DECLARATION -> InterpretFromDeclaration(statement, variables, additionalVars)
            SyntaxType.RETURN -> InterpretFromReturn(statement, variables, additionalVars)
            SyntaxType.WHILE -> InterpretFromWhile(statement, variables, additionalVars)
            SyntaxType.BREAK -> InterpretFromBreak(statement, variables, additionalVars)
            SyntaxType.CONTINUE -> InterpretFromContinue(statement, variables, additionalVars)
            SyntaxType.FOR -> InterpretFromFor(statement, variables, additionalVars)
            SyntaxType.TYPEDEF -> InterpretFromTypeDef(statement, variables, additionalVars)

            SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.TOKEN,
                SyntaxType.TYPE, SyntaxType.METHOD_CALL, null -> throw InternalErrorException()
        }
        interpreter.interpret()
    }
}