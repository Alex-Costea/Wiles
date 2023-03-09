package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.BreakSignal
import wiles.interpreter.exceptions.ContinueSignal
import wiles.interpreter.statics.InterpreterConstants.MAXINT64_REF
import wiles.interpreter.statics.InterpreterConstants.ZERO_REF
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.TypeConstants.INT64_TYPE

class InterpretFromFor(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars)
{
    @Suppress("UNCHECKED_CAST")
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

        val collection = if(inCollection != null) {
            val inferFromCollection = InterpretFromExpression(inCollection, variables, additionalVars)
            inferFromCollection.interpret()
            inferFromCollection.reference
        } else null

        val fromValue = if(fromExpression == null) ZERO_REF else
        {
            val interpreter = InterpretFromExpression(fromExpression ,variables, additionalVars)
            interpreter.interpret()
            interpreter.reference
        }

        val toValue = if(toExpression == null) MAXINT64_REF else
        {
            val interpreter = InterpretFromExpression(toExpression ,variables, additionalVars)
            interpreter.interpret()
            interpreter.reference
        }

        val range = if(fromValue.value as Long > toValue.value as Long)
            ((fromValue.value as Long) downTo (toValue.value as Long))
            else (fromValue.value as Long until toValue.value as Long)

        val newCollection = collection?.clone()

        for(i in range)
        {
            variables[name] = if(newCollection == null) ObjectDetails(i, INT64_TYPE)
            else (newCollection.value as MutableList<ObjectDetails>).getOrNull(i.toInt()) ?: break

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