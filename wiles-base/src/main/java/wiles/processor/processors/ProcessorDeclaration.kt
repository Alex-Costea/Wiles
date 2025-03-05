package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.InferenceFailureException
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

class ProcessorDeclaration(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext,
) : AbstractProcessor(syntax, context) {
    override fun process() {
        val components = syntax.components.toMutableList()
        val typeDef = if(components[0].syntaxType == SyntaxType.TYPEDEF) components.removeAt(0) else null
        val nameToken = components[0]
        val name = nameToken.details[0]
        val expression = components.getOrNull(1)
        val isCheckingLevelScope = getIsCheckingLevelScope(name)
        val newContext = if(isCheckingLevelScope) createContext() else context
        val valueAlreadyKnown = newContext.values[name]?.isKnown() == true

        if(expression == null)
            TODO("Handle no expression body")

        if (!valueAlreadyKnown) {
            if(newContext.compileMode && newContext.values.containsKey(name) && !isCheckingLevelScope)
            {
                throw IdentifierAlreadyDeclaredException(nameToken.getFirstLocation())
            }
            val details = syntax.details
            val isConst = details.contains(CONST_ID)

            var declaredType : AbstractType? = null
            val isLevelScoped = if(details.contains(LEVEL_SCOPE_ID)) {
                if(typeDef == null)
                    throw InferenceFailureException(nameToken.getFirstLocation())
                !isCheckingLevelScope
            } else false

            if((context.compileMode || isLevelScoped) && typeDef != null)
            {
                declaredType = getDeclaredType(typeDef, context)
            }

            val processorExpression = ProcessorExpression(expression, newContext)
            val newValue = if (isLevelScoped) {
                Value(WilesLazyObject(processorExpression), declaredType!!, ValueProps.DEFAULT_EXPR)
            } else {
                processorExpression.process()
                val computedValue = processorExpression.value
                val variableStatus = if (details.contains(VARIABLE_ID)) VariableStatus.Var else VariableStatus.Const
                var newType = computedValue.getType()
                if (variableStatus == VariableStatus.Var)
                    newType = newType.removeExact()
                if (declaredType != null) {
                    if (context.isRunning || isSuperType(declaredType, newType))
                        newType = declaredType
                    else throw TypeConflictError(declaredType, newType, typeDef!!.getFirstLocation())
                }
                if (context.compileMode && isConst && !newType.isExact())
                    throw ValueNotConstException(nameToken.getFirstLocation())
                Value(computedValue.getObj(), newType, ValueProps(variableStatus))
            }
            context.values[name] = newValue
        }
    }

    private fun getDeclaredType(typeDef : AbstractSyntaxTree, context : InterpreterContext): AbstractType {
        val typeProcessor = ProcessorTypeExpression(typeDef, context)
        typeProcessor.process()
        val typeDefValue = typeProcessor.value
        assert(typeDefValue.isKnown())
        assert(isSuperType(TYPE_TYPE,typeDefValue.getType()))
        return typeDefValue.getObj() as AbstractType
    }

    private fun getIsCheckingLevelScope(name : String): Boolean {
        return context.compileMode && context.values[name]?.isLazy() == true
    }

    private fun createContext() : InterpreterContext
    {
        val tempValues = ValuesMap()
        for((key,value) in context.values.entries)
        {
            val newValue = Value(null, value.getType().removeExact(), value.getProps())
            tempValues[key] = if(value.isLazy()) newValue else value
        }
        return InterpreterContext(context.isRunning, tempValues, context.isDebug, context.exceptions)
    }

}