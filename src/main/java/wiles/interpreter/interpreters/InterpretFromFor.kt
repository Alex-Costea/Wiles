package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.interpreter.exceptions.ContinueSignal
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.TypeConstants.INT64_TYPE

class InterpretFromFor(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    override fun interpret() {
        val name = statement.components[1].name

        var compIndex = 2

        val inCollection = if(statement.components.getOrNull(compIndex)?.name == IN_ID)
        {
            compIndex+=1
            statement.components[compIndex++]
        }
        else null

        val fromExpression = if(statement.components.getOrNull(compIndex)?.name == FROM_ID)
        {
            compIndex+=1
            statement.components[compIndex++]
        }
        else null

        val toExpression = if(statement.components.getOrNull(compIndex)?.name == TO_ID)
        {
            compIndex+=1
            statement.components[compIndex++]
        }
        else null

        if(inCollection != null) TODO()

        val fromValue = if(fromExpression == null) 0L else
        {
            val interpreter = InterpretFromExpression(fromExpression ,variables, additionalVars)
            interpreter.interpret()
            interpreter.reference
        }

        val toValue = if(toExpression == null) Long.MAX_VALUE else
        {
            val interpreter = InterpretFromExpression(toExpression ,variables, additionalVars)
            interpreter.interpret()
            interpreter.reference
        }

        val newRef = newReference()
        variables[name] = newRef

        for(i in objectsMap[fromValue]!!.value as Long .. objectsMap[toValue]!!.value as Long)
        {
            objectsMap[newRef] = ObjectDetails(i, INT64_TYPE)
            val inferrer = InterpretFromCodeBlock(statement.components[compIndex], variables, additionalVars)
            try
            {
                inferrer.interpret()
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