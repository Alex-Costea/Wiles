package wiles.interpreter.interpreters

import wiles.interpreter.services.InterpreterService
import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement

class InterpretFromCodeBlock(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap) :
    InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        for(component in statement.components)
        {
            InterpreterService(component, variables, additionalVars).interpret()
        }
    }
}