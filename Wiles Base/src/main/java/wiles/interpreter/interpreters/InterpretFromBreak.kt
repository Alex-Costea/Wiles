package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.shared.JSONStatement

class InterpretFromBreak(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, additionalVars, context)
{
    override fun interpret() {
        throw BreakSignal()
    }
}