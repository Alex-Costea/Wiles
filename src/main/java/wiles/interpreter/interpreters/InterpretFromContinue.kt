package wiles.interpreter.interpreters

import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.ContinueSignal
import wiles.shared.JSONStatement

class InterpretFromContinue(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        throw ContinueSignal()
    }
}