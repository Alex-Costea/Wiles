package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.IDENTIFIER_START
import wiles.shared.constants.TypeConstants.DATA_TYPE

class InterpretFromData(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpreterWithRef(statement, variables,context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val dict = linkedMapOf<ObjectDetails, ObjectDetails>()
        var index = 0
        val type = DATA_TYPE.copy()
        while(index < statement.components.size)
        {
            val statement1 = statement.components[index]
            val interpreter1 = InterpretFromExpression(statement1, variables, context)
            interpreter1.interpret()

            index++

            val statement2 = statement.components[index]
            val interpreter2 = InterpretFromExpression(statement2, variables, context)
            interpreter2.interpret()

            dict[interpreter1.reference] = interpreter2.reference
            index++

            val identifierStatement = statement1.copy().components[0]
            identifierStatement.name = IDENTIFIER_START + identifierStatement.name.substring(1)
            type.components.add(identifierStatement)
            type.components.add(interpreter2.reference.getType())
        }
        reference = ObjectDetails(dict, type)
    }
}