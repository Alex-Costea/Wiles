package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.services.InterpreterService
import wiles.shared.JSONStatement

class InterpretFromCodeBlock(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext) :
    InterpretFromStatement(statement, variables, additionalVars, context)
{
    override fun interpret() {
        for(component in statement.components)
        {
            InterpreterService(component, variables, additionalVars, context).interpret()
        }
    }
}