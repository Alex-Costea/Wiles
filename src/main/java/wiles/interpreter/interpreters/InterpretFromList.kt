package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.TypeUtils.makeList
import wiles.shared.constants.Types.EITHER_ID

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
                interpreter.reference.type
            else addType(newType, interpreter.reference.type)
        }
        reference = ObjectDetails(list, makeList((newType?:statement.components[0].copyRemovingLocation())))
    }

    private fun addType(resultingType: JSONStatement, addedType: JSONStatement): JSONStatement {
        return if(isFormerSuperTypeOfLatter(resultingType,addedType))
            resultingType
        else if(isFormerSuperTypeOfLatter(addedType, resultingType))
            addedType
        else if(resultingType.name == EITHER_ID) {
            resultingType.components.add(addedType)
            resultingType
        } else JSONStatement(name = EITHER_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(resultingType,addedType))
    }
}