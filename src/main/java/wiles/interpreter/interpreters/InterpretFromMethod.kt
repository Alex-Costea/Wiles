package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.StandardLibrary.NOTHING_REF
import wiles.shared.constants.StandardLibrary.defaultVariableMap
import wiles.shared.constants.Tokens.METHOD_ID
import java.util.function.Function

class InterpretFromMethod(statement: JSONStatement, variables: InterpreterVariableMap, additionalVars: InterpreterVariableMap)
    : InterpreterWithRef(statement, variables, additionalVars)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val type = statement.copyRemovingLocation()
        type.components.removeLast()
        val newVars = variables.copy()
        val codeBlock = statement.components.last()
        for(component in statement.components.dropLast(1).drop(1))
        {
            val interpreter = InterpretFromDeclaration(component, newVars, additionalVars)
            interpreter.interpret()
        }
        val defaultVars = InterpreterVariableMap()
        defaultVars.putAll(newVars.filter { it.key !in variables })
        val functionType = JSONStatement(name = METHOD_ID, type = SyntaxType.TYPE, components = mutableListOf(type))
        reference = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ givenVars ->
            val funcVars = InterpreterVariableMap()
            funcVars.putAll(defaultVars.filter { it.key !in givenVars })
            funcVars.putAll(givenVars)
            funcVars.putAll(defaultVariableMap)
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