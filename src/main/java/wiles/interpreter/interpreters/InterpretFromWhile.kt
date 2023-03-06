package wiles.interpreter.interpreters

import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.interpreter.exceptions.ContinueSignal
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.JSONStatement

class InterpretFromWhile(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        val conditionInterpreter = InterpretFromExpression(statement.components[0], variables, additionalVars)
        val codeBlockInterpreter = InterpretFromCodeBlock(statement.components[1], variables, additionalVars)

        while(true)
        {
            conditionInterpreter.interpret()

            if(objectsMap[conditionInterpreter.reference]!!.value == false)
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