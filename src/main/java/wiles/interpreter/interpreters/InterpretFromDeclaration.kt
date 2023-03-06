package wiles.interpreter.interpreters

import wiles.interpreter.data.VariableMap
import wiles.shared.JSONStatement

class InterpretFromDeclaration(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap) :
    InterpretFromStatement(statement, variables, additionalVars) {
    override fun interpret() {
        if(statement.components.size==3) {
            val interpretFromExpression = InterpretFromExpression(statement.components[2], variables, additionalVars)
            interpretFromExpression.interpret()

            assert(interpretFromExpression.reference != Long.MAX_VALUE)

            variables[statement.components[1].name] = interpretFromExpression.reference
        }
    }
}