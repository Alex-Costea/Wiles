package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement

class InterpretFromReturn(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        val interpreter = InterpretFromExpression(statement.components[0],variables, context)
        interpreter.interpret()
        throw ReturnSignal(interpreter.reference)
    }
}