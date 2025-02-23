package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.TypeConflictError
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
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
            components.removeAt(0)
            else null
        val nameToken = components[0]
        val expression = components.getOrNull(1)
        val name = nameToken.details[0]

        var typeDefType : AbstractType? = null
        if(typeDef != null)
        {
            val typeProcessor = ProcessorTypeExpression(typeDef, context)
            typeProcessor.process()
            val typeDefValue = typeProcessor.value
            if(!typeDefValue.isKnown())
                TODO("MUST BE KNOWN")
            if(!isSuperType(TYPE_TYPE,typeDefValue.getType()))
                TODO("Not correct type!")
            typeDefType = typeDefValue.getObj() as AbstractType
        }

        if(context.compileMode && context.values.containsKey(name))
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
            val variableStatus = if (details.contains(VARIABLE_ID)) VariableStatus.Var else VariableStatus.Const
            var newType = value.getType()
            if(variableStatus == VariableStatus.Var)
                newType = newType.removeExact()
            if(typeDefType != null)
            {
                if(isSuperType(typeDefType, newType))
                    newType = typeDefType
                else throw TypeConflictError(typeDefType, newType, typeDef!!.getFirstLocation())
            }
            val newValue = Value(value.getObj(), newType, ValueProps(variableStatus))
            context.values[name] = newValue
        }
    }
}