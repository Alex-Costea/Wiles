package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.services.DoOperation
import wiles.interpreter.statics.InterpreterConstants.FALSE_REF
import wiles.interpreter.statics.InterpreterConstants.NOTHING_REF
import wiles.interpreter.statics.InterpreterConstants.TRUE_REF
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.ELEM_ACCESS_ID
import wiles.shared.constants.Tokens.IMPORT_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MODIFY_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NEW_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.Types
import wiles.shared.constants.Types.METHOD_CALL_ID
import java.util.function.Function

class InterpretFromExpression(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpreterWithRef(statement, variables, additionalVars)
{
    override lateinit var reference : ObjectDetails

    private fun getFromValue(component : JSONStatement) : ObjectDetails
    {
        val name = component.name
        val type = component.type

        return if(Predicates.IS_NUMBER_LITERAL.test(name) && !name.contains(DECIMAL_DELIMITER)) {
            ObjectDetails(name.substring(1).toLong(), INT64_TYPE)
        }

        else if(Predicates.IS_NUMBER_LITERAL.test(name)) {
            ObjectDetails(name.substring(1).toDouble(), DOUBLE_TYPE)
        }

        else if(Predicates.IS_TEXT_LITERAL.test(name)) {
            ObjectDetails(name.substring(1), STRING_TYPE)
        }

        else if(Predicates.IS_IDENTIFIER.test(name)) {
            variables[name]!!
        }

        else {
            val interpreter : InterpreterWithRef = when (type) {
                SyntaxType.LIST -> {
                    InterpretFromList(component, variables, additionalVars)
                }
                SyntaxType.METHOD -> {
                    InterpretFromMethod(component, variables, additionalVars)
                }
                else -> throw InternalErrorException()
            }
            interpreter.interpret()
            interpreter.reference
        }
    }

    private fun getReference(component : JSONStatement) : ObjectDetails
    {
        return when (component.type) {
            SyntaxType.EXPRESSION -> {
                val expressionRun = InterpretFromExpression(component, variables, additionalVars)
                expressionRun.interpret()
                expressionRun.reference
            }
            else -> {
                getFromValue(component)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun interpret() {
        assert(statement.components.size == 1 || statement.components.size == 3)
        val leftStatement = statement.components[0]
        reference = when (statement.components.size) {
            1 -> {
                getFromValue(leftStatement)
            }
            3 -> {
                val rightStatement = statement.components[2]
                when(val middle = statement.components[1].name)
                {
                    ASSIGN_ID ->
                    {
                        val leftName = leftStatement.components[0].name
                        val rightRef = getReference(rightStatement)
                        variables[leftName] = rightRef
                        NOTHING_REF
                    }
                    MUTABLE_ID ->
                    {
                        val oldRef = getReference(rightStatement)
                        oldRef.makeMutable()
                    }
                    MODIFY_ID ->
                    {
                        val leftRef = getReference(leftStatement)
                        val mutableObj = getReference(rightStatement).makeMutable()
                        leftRef.type = mutableObj.type
                        leftRef.value = mutableObj.value
                        NOTHING_REF
                    }
                    OR_ID ->
                    {
                        val leftRef = getReference(leftStatement)
                        val ref : ObjectDetails = if(leftRef.value == true)
                            TRUE_REF
                        else {
                            val rightRef = getReference(rightStatement)
                            if ((rightRef.value == true))
                                TRUE_REF
                            else FALSE_REF
                        }
                        ref
                    }
                    AND_ID ->
                    {
                        val leftRef = getReference(leftStatement)
                        val ref : ObjectDetails = if(leftRef.value == false)
                            FALSE_REF
                        else {
                            val rightRef = getReference(rightStatement)
                            if (rightRef.value == false)
                                FALSE_REF
                            else TRUE_REF
                        }
                        ref
                    }
                    "${METHOD_ID}|${APPLY_ID}|${METHOD_CALL_ID}" ->
                    {
                        val leftRef = getReference(leftStatement)
                        val function = leftRef.value as Function<VariableMap, ObjectDetails>
                        val newVarMap = VariableMap()
                        for(component in rightStatement.components)
                        {
                            val name = component.components[0].name
                            val expressionRef = getReference(component.components[2])
                            newVarMap[name] = expressionRef
                        }
                        function.apply(newVarMap)
                    }
                    ELEM_ACCESS_ID -> {
                        val leftRef = getReference(leftStatement)
                        val rightRef = getReference(rightStatement)
                        val valueLong = (rightRef.value as Long)
                        val valueInt = if (valueLong >= Int.MIN_VALUE && valueLong <= Int.MAX_VALUE) valueLong.toInt()
                            else null
                        if (valueInt == null) NOTHING_REF
                        else (leftRef.value as MutableList<ObjectDetails>).getOrNull(valueInt) ?: NOTHING_REF
                    }
                    NEW_ID -> getReference(rightStatement).clone()
                    IMPORT_ID -> {
                        val newVars = variables.copy()
                        newVars.putAll(additionalVars.filter { it.key == rightStatement.name })
                        val interpreter = InterpretFromExpression(
                            JSONStatement(type =  SyntaxType.EXPRESSION, components = mutableListOf(rightStatement)),
                            newVars, additionalVars)
                        interpreter.interpret()
                        interpreter.reference
                    }
                    "${Types.LIST_ID}|${Tokens.PLUS_ID}|${Types.LIST_ID}" ->
                    {
                        val leftRef = getReference(leftStatement).clone()
                        val rightRef = getReference(rightStatement)
                        (leftRef.value as MutableList<ObjectDetails>)
                            .addAll(rightRef.value as MutableList<ObjectDetails>)
                        leftRef
                    }
                    else -> {
                        val leftRef = getReference(leftStatement)
                        val rightRef = getReference(rightStatement)
                        try
                        {
                            DoOperation.get(leftRef, middle, rightRef)
                        }
                        catch (ex : ArithmeticException)
                        {
                            throw PanicException("Cannot perform the operation " +
                                        "${leftRef.value} ${middle.split("|")[1]} ${rightRef.value}!")

                        }
                    }
                }
            }
            else -> throw InternalErrorException()
        }
    }
}