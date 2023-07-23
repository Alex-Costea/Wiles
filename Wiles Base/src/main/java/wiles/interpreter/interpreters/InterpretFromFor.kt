package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.BreakSignal
import wiles.interpreter.exceptions.ContinueSignal
import wiles.interpreter.statics.InterpreterConstants.ZERO_REF
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.TypeConstants.INT_TYPE
import java.math.BigInteger

class InterpretFromFor(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
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

        val toValue = if(toExpression == null) null else
        {
            val interpreter = InterpretFromExpression(toExpression ,variables, additionalVars)
            interpreter.interpret()
            interpreter.reference
        }

        val newCollection = collection?.clone(deep = false)

        val step = if(toValue == null || (fromValue.value as BigInteger) <= (toValue.value as BigInteger))
            BigInteger.valueOf(1) else BigInteger.valueOf(-1)

        var i = (fromValue.value as BigInteger)
        while(true)
        {
            if(step > BigInteger.ZERO)
            {
                if(toValue != null && i == toValue.value)
                    break
            }
            else if(step < BigInteger.ZERO)
            {
                if(i == toValue?.value)
                    break
            }
            variables[name] = if(newCollection == null) ObjectDetails(i, INT_TYPE)
            else (newCollection.value as MutableList<ObjectDetails>).getOrNull(i.toInt()) ?: break

            val inferrer = InterpretFromCodeBlock(statement.components[compIndex], variables, additionalVars)
            i += step
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