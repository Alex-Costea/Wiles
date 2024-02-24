package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.shared.JSONStatement

class InterpretFromBreak(statement: JSONStatement, variables: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        throw BreakSignal()
    }
}