package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.services.InterpreterService
import wiles.shared.JSONStatement

class InterpretFromCodeBlock(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext) :
    InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        for(component in statement.components)
        {
            InterpreterService(component, variables, context).interpret()
        }
    }
}