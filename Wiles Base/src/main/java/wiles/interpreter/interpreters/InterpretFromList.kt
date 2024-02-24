package wiles.interpreter.interpreters

import wiles.checker.statics.InferrerUtils.addType
import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeUtils

class InterpretFromList(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpreterWithRef(statement, variables, context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val list = mutableListOf<ObjectDetails>()
        var newType : JSONStatement? = null
        for(component in statement.components.drop(1))
        {
            val interpreter = InterpretFromExpression(component, variables, context)
            interpreter.interpret()
            list.add(interpreter.reference)
            newType = if(newType == null)
                interpreter.reference.getType()
            else addType(newType, interpreter.reference.getType())
        }
        reference = ObjectDetails(list, TypeUtils.makeList((newType ?: statement.components[0].copyRemovingLocation())))
    }
}