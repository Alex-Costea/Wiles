package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.errors.IdentifierAlreadyDeclaredException
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.GLOBAL_ID
import wiles.shared.constants.Tokens.VARIABLE_ID

class ProcessorDeclaration(
     syntax : AbstractSyntaxTree,
     context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override fun process() {
        val details = syntax.details
        val components = syntax.components.toMutableList()
        if(details.contains(VARIABLE_ID) || details.contains(CONST_ID) || details.contains(GLOBAL_ID))
            TODO("Can't handle these types of declarations yet")
        val typeDef = if(components[0].syntaxType == SyntaxType.TYPEDEF)
            components.removeFirst()
            else null
        val nameToken = components[0]
        val expression = components.getOrNull(1)
        val name = nameToken.details[0]

        if(typeDef != null)
            TODO("No type checking yet!")

        if(!context.isRunning && context.values.containsKey(name))
        {
            context.exceptions.add(IdentifierAlreadyDeclaredException(nameToken.location!!))
            return
        }

        if(expression == null)
            TODO("Handle no expression body")

        // don't process if value already known at compile time
        if (context.values[name]?.getType()?.isSingleton() != true) {
            val processorExpression = ProcessorExpression(expression, context)
            processorExpression.process()
            context.values[name] = processorExpression.value
        }
    }
}