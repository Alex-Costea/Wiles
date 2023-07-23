package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement

class InterpretFromReturn(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        val interpreter = InterpretFromExpression(statement.components[0],variables, additionalVars)
        interpreter.interpret()
        throw ReturnSignal(interpreter.reference)
    }
}