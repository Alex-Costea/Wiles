package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.interpreter.exceptions.ContinueSignal
import wiles.shared.JSONStatement

class InterpretFromWhile(statement: JSONStatement, variables: InterpreterVariableMap, context: InterpreterContext)
    : InterpretFromStatement(statement, variables, context)
{
    override fun interpret() {
        val conditionInterpreter = InterpretFromExpression(statement.components[0], variables, context)
        val codeBlockInterpreter = InterpretFromCodeBlock(statement.components[1], variables, context)

        while(true)
        {
            conditionInterpreter.interpret()

            if(conditionInterpreter.reference.value == false)
                break
            try
            {
                codeBlockInterpreter.interpret()
            }
            catch (breakSignal : BreakSignal)
            {
                break
            }
            catch (continueSignal : ContinueSignal)
            {
                continue
            }
        }
    }
}