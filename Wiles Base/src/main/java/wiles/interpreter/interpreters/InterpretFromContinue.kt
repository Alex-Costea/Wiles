package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.ContinueSignal
import wiles.shared.JSONStatement

class InterpretFromContinue(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        throw ContinueSignal()
    }
}