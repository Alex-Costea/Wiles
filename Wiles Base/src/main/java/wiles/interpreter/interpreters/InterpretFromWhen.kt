package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InterpretFromWhen(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, additionalVars, context)
{
    override fun interpret() {
        val components = statement.components.toMutableList()
        val name = components.removeFirst().components[0].name
        val objectDetails = variables[name]
        while (components.isNotEmpty())
        {
            val type = components.removeFirst()
            val expression = components.removeFirst()
            if(type.name != ELSE_ID && !isFormerSuperTypeOfLatter(type,objectDetails!!.getType()))
                continue

            val interpreter = InterpretFromCodeBlock(expression, variables, additionalVars, context)
            interpreter.interpret()
            break
        }
    }

}