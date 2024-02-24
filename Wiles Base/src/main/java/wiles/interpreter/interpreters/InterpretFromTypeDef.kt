package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Types

class InterpretFromTypeDef(statement: JSONStatement, variables: InterpreterVariableMap,
                           context: InterpreterContext
)
    : InterpretFromStatement(statement, variables,context ) {
    override fun interpret() {
        val name = statement.components[0].name
        val type = statement.components[1]
        val typeType = JSONStatement(name = Types.TYPE_TYPE_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
        variables[name] = ObjectDetails(type,typeType)
    }
}