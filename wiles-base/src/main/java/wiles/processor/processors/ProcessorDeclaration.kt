package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.values.Value
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
        if(details.contains(CONST_ID) || details.contains(GLOBAL_ID))
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
            context.exceptions.add(IdentifierAlreadyDeclaredException(nameToken.getFirstLocation()))
            return
        }

        if(expression == null)
            TODO("Handle no expression body")

        // don't process if value already known at compile time
        if (context.values[name]?.isKnown() != true) {
            val processorExpression = ProcessorExpression(expression, context)
            processorExpression.process()
            val value = processorExpression.value
            val variableStatus = if (details.contains(VARIABLE_ID)) VariableStatus.Var else VariableStatus.Val
            val newValue = Value(value.getObj(),
                value.getType().removeSingleton().clone(), ValueProps(variableStatus, value.getKnownStatus())
            )
            context.values[name] = newValue
        }
    }
}