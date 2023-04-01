package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.statics.InterpreterConstants.addType
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeUtils

class InterpretFromList(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpreterWithRef(statement, variables, additionalVars)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val list = mutableListOf<ObjectDetails>()
        var newType : JSONStatement? = null
        for(component in statement.components.drop(1))
        {
            val interpreter = InterpretFromExpression(component, variables, additionalVars)
            interpreter.interpret()
            list.add(interpreter.reference)
            newType = if(newType == null)
                interpreter.reference.getType()
            else addType(newType, interpreter.reference.getType())
        }
        reference = ObjectDetails(list, TypeUtils.makeList((newType ?: statement.components[0].copyRemovingLocation())))
    }
}