package wiles.interpreter.interpreters

import wiles.interpreter.InterpretFrom
import wiles.interpreter.VariableMap
import wiles.shared.JSONStatement

class InterpretFromCodeBlock(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap) :
    InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        for(component in statement.components)
        {
            InterpretFrom(component, variables, additionalVars).interpret()
        }
    }
}