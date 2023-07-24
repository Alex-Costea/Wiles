package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement

class InterpretFromReturn(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, additionalVars, context)
{
    override fun interpret() {
        val interpreter = InterpretFromExpression(statement.components[0],variables, additionalVars, context)
        interpreter.interpret()
        throw ReturnSignal(interpreter.reference)
    }
}