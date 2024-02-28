package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.shared.JSONStatement

abstract class InterpretFromStatement(val statement : JSONStatement,
                                      val variables : InterpreterVariableMapInterface,
                                      val context: InterpreterContext
) {
    abstract fun interpret()
}