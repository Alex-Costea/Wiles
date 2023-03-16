package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.TypeConstants

class InterpretFromWhen(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        val components = statement.components.toMutableList()
        val name = components.removeFirst().components[0].name
        val objectDetails = variables[name]
        while (components.isNotEmpty())
        {
            val type = components.removeFirst()
            val expression = components.removeFirst()
            if(type.name == ELSE_ID)
            {
                objectDetails!!.type = type.components[0]
            }
            else if(TypeConstants.isFormerSuperTypeOfLatter(type,objectDetails!!.type))
            {
                objectDetails.type = type
            }
            else continue

            val interpreter = InterpretFromCodeBlock(expression, variables, additionalVars)
            interpreter.interpret()
            break
        }
    }

}