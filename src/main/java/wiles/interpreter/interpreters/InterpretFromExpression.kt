package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.services.DoOperation
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.Predicates

class InterpretFromExpression(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars) {
    var reference : Long = Long.MAX_VALUE

    private fun getFromValue(name : String) : Long
    {
        val ref : Long
        if(Predicates.IS_NUMBER_LITERAL.test(name)) {
            ref = newReference()
            objectsMap[ref] = ObjectDetails(name.substring(1).toLong(), INT64_TYPE)
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

    override fun interpret() {
        assert(statement.components.size == 1 || statement.components.size == 3)
        if(statement.components.size == 1)
        {
            val name = statement.components[0].name
            reference = getFromValue(name)
        }
        else if(statement.components.size == 3)
        {
            val leftComp = statement.components[0]
            val middle = statement.components[1].name
            val rightComp = statement.components[2]

            val leftRef = if(leftComp.type != SyntaxType.EXPRESSION) {
                getFromValue(leftComp.name)
            } else {
                val expressionRun = InterpretFromExpression(leftComp, variables, additionalVars)
                expressionRun.interpret()
                expressionRun.reference
            }

            val rightRef = if(rightComp.type != SyntaxType.EXPRESSION) {
                getFromValue(rightComp.name)
            } else {
                val expressionRun = InterpretFromExpression(rightComp, variables, additionalVars)
                expressionRun.interpret()
                expressionRun.reference
            }

            reference = DoOperation.get(leftRef, middle, rightRef)
        }
    }
}