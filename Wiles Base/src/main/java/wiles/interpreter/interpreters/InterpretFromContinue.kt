package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.ContinueSignal
import wiles.shared.JSONStatement

class InterpretFromContinue(statement: JSONStatement, variables: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        throw ContinueSignal()
    }
}