package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.exceptions.BreakSignal
import wiles.shared.JSONStatement

class InterpretFromBreak(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        throw BreakSignal()
    }
}