package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement

abstract class InterpreterWithRef(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    abstract var reference : ObjectDetails
}