package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.JSONStatement
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL

class InterpretFromExpression(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars) {
    var reference : Long = Long.MAX_VALUE
    override fun interpret() {
        assert(statement.components.size == 1 || statement.components.size == 3)
        if(statement.components.size == 1)
        {
            val name = statement.components[0].name
            if(IS_NUMBER_LITERAL.test(name)) {
                reference = newReference()
                objectsMap[reference] = ObjectDetails(name.substring(1).toLong(), INT64_TYPE)
            }

            else if(IS_TEXT_LITERAL.test(name)) {
                reference = newReference()
                objectsMap[reference] = ObjectDetails(name.substring(1), STRING_TYPE)
            }

            else if(IS_IDENTIFIER.test(name)) {
                reference = variables[name]!!.reference
            }

            else TODO()
        }
        else TODO()
    }
}