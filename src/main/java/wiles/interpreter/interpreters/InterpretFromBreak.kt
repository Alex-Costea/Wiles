package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.shared.JSONStatement

class InterpretFromBreak(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        throw BreakSignal()
    }
}