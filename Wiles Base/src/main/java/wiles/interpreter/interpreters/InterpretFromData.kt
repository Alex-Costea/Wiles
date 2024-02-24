package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement

class InterpretFromData(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext)
    : InterpreterWithRef(statement, variables, additionalVars,context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        TODO()
    }
}