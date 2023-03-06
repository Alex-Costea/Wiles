package wiles.interpreter.interpreters

import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.shared.JSONStatement

class InterpretFromBreak(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        throw BreakSignal()
    }
}