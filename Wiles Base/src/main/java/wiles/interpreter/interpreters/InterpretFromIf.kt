package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.ELSE_ID

class InterpretFromIf(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, additionalVars, context)
{
    override fun interpret() {
        for(i in statement.components.indices step 2)
        {
            val expression = statement.components[i]
            val codeBlock = statement.components[i+1]
            if(expression.name == ELSE_ID)
            {
                InterpretFromCodeBlock(codeBlock, variables, additionalVars, context).interpret()
            }
            else
            {
                val expressionInterpreter = InterpretFromExpression(expression, variables, additionalVars, context)
                expressionInterpreter.interpret()
                if(expressionInterpreter.reference.value == true)
                {
                    InterpretFromCodeBlock(codeBlock, variables, additionalVars, context).interpret()
                    break
                }
            }
        }
    }
}