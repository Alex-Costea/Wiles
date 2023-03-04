package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.services.DoOperation
import wiles.interpreter.statics.InterpreterConstants.FALSE_REF
import wiles.interpreter.statics.InterpreterConstants.NOTHING_REF
import wiles.interpreter.statics.InterpreterConstants.TRUE_REF
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars.DECIMAL_DELIMITER
import wiles.shared.constants.CheckerConstants.DOUBLE_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.OR_ID

class InterpretFromExpression(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars) {
    var reference : Long = Long.MAX_VALUE

    private fun getFromValue(name : String) : Long
    {
        val ref : Long
        if(Predicates.IS_NUMBER_LITERAL.test(name) && !name.contains(DECIMAL_DELIMITER)) {
            ref = newReference()
            objectsMap[ref] = ObjectDetails(name.substring(1).toLong(), INT64_TYPE)
        }

        else if(Predicates.IS_NUMBER_LITERAL.test(name)) {
            ref = newReference()
            objectsMap[ref] = ObjectDetails(name.substring(1).toDouble(), DOUBLE_TYPE)
        }

        else if(Predicates.IS_TEXT_LITERAL.test(name)) {
            ref = newReference()
            objectsMap[ref] = ObjectDetails(name.substring(1), STRING_TYPE)
        }

        else if(Predicates.IS_IDENTIFIER.test(name)) {
            ref = variables[name]!!.reference
        }

        else TODO()
        return ref
    }

    private fun getReference(component : JSONStatement) : Long
    {
        return when (component.type) {
            SyntaxType.TOKEN -> {
                getFromValue(component.name)
            }
            SyntaxType.EXPRESSION -> {
                val expressionRun = InterpretFromExpression(component, variables, additionalVars)
                expressionRun.interpret()
                expressionRun.reference
            }
            else -> TODO()
        }
    }

    override fun interpret() {
        assert(statement.components.size == 1 || statement.components.size == 3)
        val leftStatement = statement.components[0]
        reference = when (statement.components.size) {
            1 -> {
                getFromValue(leftStatement.name)
            }
            //TODO: apply, elem access, mutable, import, modify
            3 -> {
                val rightStatement = statement.components[2]
                when(val middle = statement.components[1].name)
                {
                    ASSIGN_ID->
                    {
                        val leftName = leftStatement.components[0].name
                        val rightRef = getReference(rightStatement)
                        variables[leftName] = VariableDetails(rightRef, objectsMap[rightRef]!!.type)
                        NOTHING_REF
                    }
                    OR_ID ->
                    {
                        val leftRef = getReference(leftStatement)
                        val ref = if(objectsMap[leftRef]!!.value == true)
                            TRUE_REF
                        else {
                            val rightRef = getReference(rightStatement)
                            if ((objectsMap[rightRef]!!.value == true))
                                TRUE_REF
                            else FALSE_REF
                        }
                        ref
                    }
                    AND_ID ->
                    {
                        val leftRef = getReference(leftStatement)
                        val ref = if(objectsMap[leftRef]!!.value == false)
                            FALSE_REF
                        else {
                            val rightRef = getReference(rightStatement)
                            if ((objectsMap[rightRef]!!.value == false))
                                FALSE_REF
                            else TRUE_REF
                        }
                        ref
                    }
                    else -> {
                        val leftRef = getReference(leftStatement)
                        if (middle.contains("|${OR_ID}|") && objectsMap[leftRef]!!.value == true) {
                            TRUE_REF
                        } else if (middle.contains("|${AND_ID}|") && objectsMap[leftRef]!!.value == false) {
                            FALSE_REF
                        } else {
                            val rightRef = getReference(rightStatement)
                            DoOperation.get(leftRef, middle, rightRef)
                        }
                    }
                }
            }
            else -> throw InternalErrorException()
        }
    }
}