package wiles.interpreter.interpreters

import wiles.checker.data.GenericTypesMap
import wiles.checker.statics.InferrerUtils.makeGeneric
import wiles.interpreter.data.InterpreterContext
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
import java.util.function.BiFunction

class InterpretFromMethod(
    statement: JSONStatement, variables: InterpreterVariableMap,
    context: InterpreterContext
)
    : InterpreterWithRef(statement, variables, context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val type = statement.copyRemovingLocation()
        type.components.removeLast()
        val vars = variables.copy()
        val codeBlock = statement.components.last()
        for(component in statement.components.dropLast(1).drop(1))
        {
            val interpreter = InterpretFromDeclaration(component, vars, context)
            interpreter.interpret()
        }
        val functionType = JSONStatement(name = METHOD_ID, syntaxType = SyntaxType.TYPE, components = mutableListOf(type))
        reference = ObjectDetails(BiFunction<InterpreterVariableMap, InterpreterContext, ObjectDetails>{ givenVars, _ ->
            val funcVars = InterpreterVariableMap()
            funcVars.putAll(vars.filter { it.key !in givenVars })
            funcVars.putAll(givenVars)
            val genericTypesMap = GenericTypesMap()
            for(component in type.components)
            {
                if(component.syntaxType == SyntaxType.TYPE)
                    continue
                val name = component.components[1].name
                val superType = component.components[0]
                require(isFormerSuperTypeOfLatter(superType,funcVars[name]!!.getType(), genericTypes = genericTypesMap))
            }
            funcVars.putAll(genericTypesMap.map {
                val genericValue = makeGeneric(it.value.statement,it.key)
                Pair(it.key.split("|")[0], ObjectDetails(genericValue,
                JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.TYPE_TYPE_ID,
                    components = mutableListOf(genericValue)))) })
            funcVars.putAll(defaultInterpreterVars)
            try
            {
                val interpreter = InterpretFromCodeBlock(codeBlock, funcVars, context)
                interpreter.interpret()
                return@BiFunction NOTHING_REF
            }
            catch (ret : ReturnSignal)
            {
                return@BiFunction ret.value
            }
        }, functionType)
    }
}