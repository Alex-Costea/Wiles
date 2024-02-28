package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.shared.JSONStatement

class InterpretFromDeclaration(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext) :
    InterpretFromStatement(statement, variables, context) {
    override fun interpret() {
        if(statement.components.size==3) {
            val interpretFromExpression = InterpretFromExpression(statement.components[2], variables, context)
            interpretFromExpression.interpret()

            variables.declare(statement.components[1].name,interpretFromExpression.reference)
        }
    }
}