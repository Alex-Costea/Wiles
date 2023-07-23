package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement

abstract class InterpretFromStatement(val statement : JSONStatement,
                                      val variables : InterpreterVariableMap,
                                      val additionalVars : InterpreterVariableMap
) {
    abstract fun interpret()
}