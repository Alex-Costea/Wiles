package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.TypeConflictError
import wiles.processor.errors.ValueNotConstError
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
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
        //TODO: figure out what should only be done compile-time
        val details = syntax.details
        val components = syntax.components.toMutableList()
        if(details.contains(GLOBAL_ID))
            TODO("Can't handle global declarations yet")
        val isConst = details.contains(CONST_ID)
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
                throw ValueNotConstError(nameToken.getFirstLocation())
            assert(isSuperType(TYPE_TYPE,typeDefValue.getType()))
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

            if(context.compileMode && isConst && !newType.isExact())
                throw ValueNotConstError(nameToken.getFirstLocation())
        }
    }
}