package wiles.interpreter.interpreters

import wiles.checker.data.GenericTypesMap
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.StandardLibrary.NOTHING_REF
import wiles.shared.constants.StandardLibrary.defaultInterpreterVars
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types
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
        val functionType = JSONStatement(name = METHOD_ID, syntaxType = SyntaxType.TYPE, components = mutableListOf(type))
        reference = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ givenVars ->
            val funcVars = InterpreterVariableMap()
            funcVars.putAll(defaultVars.filter { it.key !in givenVars })
            funcVars.putAll(givenVars)
            val genericTypesMap = GenericTypesMap()
            for(component in type.components)
            {
                if(component.syntaxType == SyntaxType.TYPE)
                    continue
                val name = component.components[1].name
                val superType = component.components[0]
                require(isFormerSuperTypeOfLatter(superType,funcVars[name]!!.type, genericTypes = genericTypesMap))
            }
            funcVars.putAll(genericTypesMap.map { Pair(it.key.split("|")[0], ObjectDetails(it.value.first,
                JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.TYPE_TYPE_ID,
                    components = mutableListOf(it.value.first)))) })
            funcVars.putAll(defaultInterpreterVars)
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