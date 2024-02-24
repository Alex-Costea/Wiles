package wiles.interpreter.interpreters

import wiles.checker.data.GenericTypesMap
import wiles.checker.statics.InferrerUtils.makeGeneric
import wiles.interpreter.data.*
import wiles.interpreter.exceptions.ReturnSignal
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.StandardLibrary.NOTHING_REF
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types
import java.util.function.BiFunction

class InterpretFromMethod(
    statement: JSONStatement, variables: InterpreterVariableMapInterface,
    context: InterpreterContext
)
    : InterpreterWithRef(statement, variables, context)
{
    override lateinit var reference : ObjectDetails
    override fun interpret() {
        val type = statement.copyRemovingLocation()
        type.components.removeLast()
        val codeBlock = statement.components.last()
        val functionDeclarationVars = variables.copy()
        for(component in statement.components.dropLast(1).drop(1))
        {
            val interpreter = InterpretFromDeclaration(component, functionDeclarationVars, context)
            interpreter.interpret()
        }
        val uniqueFunctionDeclarationVars = functionDeclarationVars.filter { !variables.containsKey(it.key) }
        val functionType = JSONStatement(name = METHOD_ID, syntaxType = SyntaxType.TYPE, components = mutableListOf(type))
        reference = ObjectDetails(BiFunction<InterpreterVariableMap, InterpreterContext, ObjectDetails>{ givenVars, _ ->
            val innerVars = InterpreterVariableMap()
            innerVars.putAll(givenVars)
            innerVars.putAll(uniqueFunctionDeclarationVars)
            val funcVars= ComplexInterpreterVariableMap(innerVars, variables)
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