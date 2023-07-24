package wiles.interpreter.services

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.interpreters.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InterpreterService(val statement : JSONStatement,
                         val variables : InterpreterVariableMap,
                         val additionalVars : InterpreterVariableMap,
                         val context: InterpreterContext
) {
    fun interpret()
    {
        val interpreter = when(statement.syntaxType) {
            SyntaxType.EXPRESSION -> InterpretFromExpression(statement, variables, additionalVars, context)
            SyntaxType.CODE_BLOCK -> InterpretFromCodeBlock(statement, variables, additionalVars, context)
            SyntaxType.IF -> InterpretFromIf(statement, variables, additionalVars, context)
            SyntaxType.WHEN -> InterpretFromWhen(statement, variables, additionalVars, context)
            SyntaxType.DECLARATION -> InterpretFromDeclaration(statement, variables, additionalVars, context)
            SyntaxType.RETURN -> InterpretFromReturn(statement, variables, additionalVars, context)
            SyntaxType.WHILE -> InterpretFromWhile(statement, variables, additionalVars, context)
            SyntaxType.BREAK -> InterpretFromBreak(statement, variables, additionalVars, context)
            SyntaxType.CONTINUE -> InterpretFromContinue(statement, variables, additionalVars, context)
            SyntaxType.FOR -> InterpretFromFor(statement, variables, additionalVars, context)
            SyntaxType.TYPEDEF -> InterpretFromTypeDef(statement, variables, additionalVars, context)

            SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.TOKEN, SyntaxType.DICT,
                SyntaxType.TYPE, SyntaxType.METHOD_CALL, null -> throw InternalErrorException()
        }
        interpreter.interpret()
    }
}