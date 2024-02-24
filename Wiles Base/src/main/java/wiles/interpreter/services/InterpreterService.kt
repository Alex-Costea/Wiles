package wiles.interpreter.services

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.interpreters.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InterpreterService(val statement : JSONStatement,
                         val variables : InterpreterVariableMap,
                         val context: InterpreterContext
) {
    fun interpret()
    {
        val interpreter = when(statement.syntaxType) {
            SyntaxType.EXPRESSION -> InterpretFromExpression(statement, variables, context)
            SyntaxType.CODE_BLOCK -> InterpretFromCodeBlock(statement, variables, context)
            SyntaxType.IF -> InterpretFromIf(statement, variables, context)
            SyntaxType.WHEN -> InterpretFromWhen(statement, variables, context)
            SyntaxType.DECLARATION -> InterpretFromDeclaration(statement, variables, context)
            SyntaxType.RETURN -> InterpretFromReturn(statement, variables, context)
            SyntaxType.WHILE -> InterpretFromWhile(statement, variables, context)
            SyntaxType.BREAK -> InterpretFromBreak(statement, variables, context)
            SyntaxType.CONTINUE -> InterpretFromContinue(statement, variables, context)
            SyntaxType.FOR -> InterpretFromFor(statement, variables, context)
            SyntaxType.TYPEDEF -> InterpretFromTypeDef(statement, variables, context)

            SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.TOKEN, SyntaxType.DICT, SyntaxType.DATA,
                SyntaxType.TYPE, SyntaxType.METHOD_CALL, null -> throw InternalErrorException()
        }
        interpreter.interpret()
    }
}