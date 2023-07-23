package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement

abstract class InterpreterWithRef(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    abstract var reference : ObjectDetails
}