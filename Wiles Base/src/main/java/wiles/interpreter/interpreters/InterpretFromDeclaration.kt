package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement

class InterpretFromDeclaration(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap) :
    InterpretFromStatement(statement, variables, additionalVars) {
    override fun interpret() {
        if(statement.components.size==3) {
            val interpretFromExpression = InterpretFromExpression(statement.components[2], variables, additionalVars)
            interpretFromExpression.interpret()

            variables[statement.components[1].name] = interpretFromExpression.reference
        }
    }
}