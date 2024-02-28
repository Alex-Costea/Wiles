package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMapInterface
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.ELSE_ID

class InterpretFromIf(statement: JSONStatement, variables: InterpreterVariableMapInterface, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        for(i in statement.components.indices step 2)
        {
            val expression = statement.components[i]
            val codeBlock = statement.components[i+1]
            if(expression.name == ELSE_ID)
            {
                InterpretFromCodeBlock(codeBlock, variables, context).interpret()
            }
            else
            {
                val expressionInterpreter = InterpretFromExpression(expression, variables, context)
                expressionInterpreter.interpret()
                if(expressionInterpreter.reference.value == true)
                {
                    InterpretFromCodeBlock(codeBlock, variables, context).interpret()
                    break
                }
            }
        }
    }
}