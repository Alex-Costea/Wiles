package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterConstants.FALSE_REF
import wiles.interpreter.InterpreterConstants.NOTHING_REF
import wiles.interpreter.InterpreterConstants.TRUE_REF
import wiles.interpreter.InterpreterConstants.maxReference
import wiles.interpreter.InterpreterConstants.objectsMap
import wiles.interpreter.VariableMap
import wiles.shared.JSONStatement
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID

class InterpretFromExpression(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpretFromStatement(statement, variables, additionalVars) {
    var reference : Long = -1
    override fun interpret() {
        assert(statement.components.size == 1 || statement.components.size == 3)
        if(statement.components.size == 1)
        {
            val name = statement.components[0].name
            if(IS_NUMBER_LITERAL.test(name)) {
                maxReference++
                objectsMap[maxReference] = name.substring(1).toLong()
                reference = maxReference
            }

            else if(IS_TEXT_LITERAL.test(name)) {
                maxReference++
                objectsMap[maxReference] = name.substring(1)
                reference = maxReference
            }

            else if(name == NOTHING_ID)
                reference = NOTHING_REF

            else if(name == TRUE_ID)
                reference = TRUE_REF

            else if(name == FALSE_ID)
                reference = FALSE_REF

            else TODO()
        }
        else TODO()
    }
}