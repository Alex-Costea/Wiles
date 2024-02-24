package wiles.interpreter.interpreters

import wiles.checker.statics.InferrerUtils
import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeUtils.makeDict

class InterpretFromDict(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpreterWithRef(statement, variables,context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val dict = linkedMapOf<ObjectDetails, ObjectDetails>()
        var newType1 : JSONStatement? = null
        var newType2 : JSONStatement? = null
        var index = 1
        while(index < statement.components.size)
        {
            val interpreter1 = InterpretFromExpression(statement.components[index], variables, context)
            interpreter1.interpret()

            newType1 = if(newType1 == null)
                interpreter1.reference.getType()
            else InferrerUtils.addType(newType1, interpreter1.reference.getType())

            index++

            val interpreter2 = InterpretFromExpression(statement.components[index], variables, context)
            interpreter2.interpret()

            newType2 = if(newType2 == null)
                interpreter2.reference.getType()
            else InferrerUtils.addType(newType2, interpreter2.reference.getType())

            dict[interpreter1.reference] = interpreter2.reference
            index++
        }
        reference = ObjectDetails(dict, if(newType1 == null) statement.components[0].copyRemovingLocation()
                    else makeDict(newType1, newType2!!))

    }
}