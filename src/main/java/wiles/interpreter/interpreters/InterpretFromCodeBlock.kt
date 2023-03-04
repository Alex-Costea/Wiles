package wiles.interpreter.interpreters

import wiles.interpreter.VariableMap
import wiles.shared.JSONStatement

class InterpretFromCodeBlock(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap) :
    InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        TODO("Not yet implemented")
    }
}