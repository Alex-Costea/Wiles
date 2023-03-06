package wiles.interpreter.interpreters

import wiles.interpreter.data.VariableMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.ELSE_ID

class InterpretFromIf(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        for(i in statement.components.indices step 2)
        {
            val expression = statement.components[i]
            val codeBlock = statement.components[i+1]
            if(expression.name == ELSE_ID)
            {
                InterpretFromCodeBlock(codeBlock, variables, additionalVars).interpret()
            }
            else
            {
                val expressionInterpreter = InterpretFromExpression(expression, variables, additionalVars)
                expressionInterpreter.interpret()
                if(expressionInterpreter.reference.value == true)
                {
                    InterpretFromCodeBlock(codeBlock, variables, additionalVars).interpret()
                    break
                }
            }
        }
    }
}