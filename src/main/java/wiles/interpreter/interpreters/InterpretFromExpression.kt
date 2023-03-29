package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.statics.DoOperation
import wiles.interpreter.statics.InterpreterConstants.toIntOrNull
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.ErrorMessages.CANNOT_PERFORM_OPERATION_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.StandardLibrary.FALSE_REF
import wiles.shared.constants.StandardLibrary.NOTHING_REF
import wiles.shared.constants.StandardLibrary.TRUE_REF
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.ELEM_ACCESS_ID
import wiles.shared.constants.Tokens.IMPORT_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import java.util.function.Function

class InterpretFromExpression(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpreterWithRef(statement, variables, additionalVars)
{
    override lateinit var reference : ObjectDetails

    private fun getFromValue(component : JSONStatement) : ObjectDetails
    {
        val name = component.name
        val type = component.syntaxType

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
        return when (component.syntaxType) {
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
                        if(!leftName.startsWith("!$IMPORT_ID"))
                            variables[leftName] = rightRef
                        else additionalVars[leftName.split("!$IMPORT_ID")[1]] = rightRef
                        NOTHING_REF
                    }
                    MUTABLE_ID ->
                    {
                        val oldRef = getReference(rightStatement)
                        oldRef.makeMutable()
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
                        val function = leftRef.value as Function<InterpreterVariableMap, ObjectDetails>
                        val newVarMap = InterpreterVariableMap()
                        for(component in rightStatement.components)
                        {
                            val name = component.components[0].name
                            val expressionRef = getReference(component.components[2])
                            newVarMap[name] = expressionRef
                        }
                        function.apply(newVarMap)
                    }
                    "$LIST_ID|$ELEM_ACCESS_ID|$INT64_ID" -> {
                        val leftRef = getReference(leftStatement)
                        val rightRef = getReference(rightStatement)
                        val value = (rightRef.value as Long).toIntOrNull()
                        if (value == null) NOTHING_REF
                        else (leftRef.value as MutableList<ObjectDetails>).getOrNull(value) ?: NOTHING_REF
                    }
                    IMPORT_ID -> {
                        val newVars = variables.copy()
                        newVars.putAll(additionalVars.filter { it.key == rightStatement.name })
                        val interpreter = InterpretFromExpression(
                            JSONStatement(syntaxType =  SyntaxType.EXPRESSION, components = mutableListOf(rightStatement)),
                            newVars, additionalVars)
                        interpreter.interpret()
                        interpreter.reference
                    }
                    "$LIST_ID|${Tokens.PLUS_ID}|$LIST_ID" ->
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
                            throw PanicException(CANNOT_PERFORM_OPERATION_ERROR.format(
                                "${leftRef.value}", middle.split("|")[1], "${rightRef.value}"))

                        }
                    }
                }
            }
            else -> throw InternalErrorException()
        }
    }
}
