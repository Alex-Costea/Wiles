package wiles.interpreter.interpreters

import wiles.interpreter.VariableDetails
import wiles.interpreter.VariableMap
import wiles.shared.JSONStatement

class InterpretFromDeclaration(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap) :
    InterpretFromStatement(statement, variables, additionalVars) {
    override fun interpret() {
        val interpretFromExpression = InterpretFromExpression(statement.components[2], variables, additionalVars)
        interpretFromExpression.interpret()

        variables[statement.components[1].name] = VariableDetails(
            reference = interpretFromExpression.reference,
            type = statement.components[0].copyRemovingLocation()
        )
    }
}