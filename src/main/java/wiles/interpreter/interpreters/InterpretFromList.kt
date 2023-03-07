package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeConstants.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types.EITHER_ID

class InterpretFromList(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    lateinit var reference : ObjectDetails
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
        reference = ObjectDetails(list, newType?:statement.components[0])
    }

    private fun addType(resultingType: JSONStatement, addedType: JSONStatement): JSONStatement {
        return if(isFormerSuperTypeOfLatter(resultingType,addedType))
            resultingType
        else if(isFormerSuperTypeOfLatter(addedType, resultingType))
            addedType
        else if(resultingType.name == EITHER_ID) {
            resultingType.components.add(addedType)
            resultingType
        } else JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE,
            components = mutableListOf(resultingType,addedType))
    }
}