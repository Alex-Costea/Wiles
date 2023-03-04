package wiles.interpreter.interpreters

import wiles.interpreter.VariableMap
import wiles.shared.JSONStatement

abstract class InterpretFromStatement(val statement : JSONStatement,
                                      val variables : VariableMap,
                                      val additionalVars : VariableMap
) {
    abstract fun interpret()
}