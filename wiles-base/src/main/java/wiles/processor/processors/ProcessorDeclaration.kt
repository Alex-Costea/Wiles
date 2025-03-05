package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.TypeConflictError
import wiles.processor.errors.ValueNotConstException
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
import wiles.processor.values.WilesLazyObject
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.constants.Tokens.VARIABLE_ID

open class ProcessorDeclaration(
     syntax : AbstractSyntaxTree,
     context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    protected open val isCheckingLevelScope = false
    override fun process() {
        //TODO: figure out what should only be done compile-time
        val details = syntax.details
        val components = syntax.components.toMutableList()
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
            assert(typeDefValue.isKnown())
            assert(isSuperType(TYPE_TYPE,typeDefValue.getType()))
            typeDefType = typeDefValue.getObj() as AbstractType
        }

        if(context.compileMode && context.values.containsKey(name) && !isCheckingLevelScope)
        {
            throw IdentifierAlreadyDeclaredException(nameToken.getFirstLocation())
        }

        if(expression == null)
            TODO("Handle no expression body")

        val isLazy = if(details.contains(LEVEL_SCOPE_ID)) {
            if(typeDef == null)
                TODO("This level scope declaration requires a type definition.")
            !isCheckingLevelScope
        } else false

        // don't process if value already known at compile time
        if (context.values[name]?.isKnown() != true) {
            val processorExpression = ProcessorExpression(expression, context)
            val value : Value = if (isLazy) {
                Value(WilesLazyObject(processorExpression), typeDefType!!, ValueProps.DEFAULT_EXPR)
            } else {
                processorExpression.process()
                processorExpression.value
            }
            if (isLazy) {
                context.values[name] = value
            } else {
                val variableStatus = if (details.contains(VARIABLE_ID)) VariableStatus.Var else VariableStatus.Const
                var newType = value.getType()
                if(variableStatus == VariableStatus.Var)
                    newType = newType.removeExact()
                if(typeDefType != null) {
                    if(isSuperType(typeDefType, newType))
                        newType = typeDefType
                    else throw TypeConflictError(typeDefType, newType, typeDef!!.getFirstLocation())
                }
                val newValue = Value(value.getObj(), newType, ValueProps(variableStatus))
                context.values[name] = newValue
                if(context.compileMode && isConst && !newType.isExact())
                    throw ValueNotConstException(nameToken.getFirstLocation())
            }

        }
    }
}