package wiles.interpreter.interpreters

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.ReturnSignal
import wiles.interpreter.statics.InterpreterConstants
import wiles.interpreter.statics.InterpreterConstants.NOTHING_REF
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import java.util.function.Function

class InterpretFromMethod(statement: JSONStatement, variables: VariableMap, additionalVars: VariableMap)
    : InterpreterWithRef(statement, variables, additionalVars)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val type = statement.copyRemovingLocation()
        type.components.removeLast()
        val newVars = variables.copy()
        val codeBlock = statement.components.last()
        for(component in statement.components.dropLast(1))
        {
            val interpreter = InterpretFromDeclaration(component, newVars, additionalVars)
            interpreter.interpret()
        }
        val defaultVars = VariableMap()
        defaultVars.putAll(newVars.filter { it.key !in variables })
        val functionType = JSONStatement(name = METHOD_ID, type = SyntaxType.TYPE, components = mutableListOf(type))
        if(type.components.isEmpty())
            functionType.components[0].components.add(NOTHING_TYPE)
        reference = ObjectDetails(Function<VariableMap, ObjectDetails>{ givenVars ->
            val funcVars = VariableMap()
            funcVars.putAll(defaultVars.filter { it.key !in givenVars })
            funcVars.putAll(givenVars)
            funcVars.putAll(InterpreterConstants.defaultVariableMap)
            try
            {
                val interpreter = InterpretFromCodeBlock(codeBlock,funcVars, variables)
                interpreter.interpret()
                return@Function NOTHING_REF
            }
            catch (ret : ReturnSignal)
            {
                return@Function ret.value
            }
        }, functionType)
    }
}