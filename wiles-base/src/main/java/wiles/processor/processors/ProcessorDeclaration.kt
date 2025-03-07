package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.InferenceFailureException
import wiles.processor.errors.TypeConflictError
import wiles.processor.errors.ValueNotConstException
import wiles.processor.processors.Processor.Companion.NOTHING_VALUE
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
import wiles.processor.values.WilesLazyObject
import wiles.processor.values.WilesUndefined
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.enums.SyntaxType

class ProcessorDeclaration(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext,
) : AbstractProcessor(syntax, context) {
    override var value: Value = NOTHING_VALUE
    override fun process() {
        val components = syntax.getComponents().toMutableList()
        val typeDef = if(components[0].syntaxType == SyntaxType.TYPEDEF) components.removeAt(0) else null
        val nameToken = components[0]
        val name = nameToken.details[0]
        val expression = components.getOrNull(1)
        val isCheckingLevelScope = getIsCheckingLevelScope(name)
        val newContext = if(isCheckingLevelScope) createContext() else context
        val valueAlreadyKnown = newContext.values[name]?.isKnown() == true
        val details = syntax.details
        val variableStatus = if (details.contains(VARIABLE_ID)) VariableStatus.Var else VariableStatus.Const

        if(newContext.isCompiling && newContext.values.containsKey(name) && !isCheckingLevelScope)
        {
            throw IdentifierAlreadyDeclaredException(nameToken.getFirstLocation())
        }

        if(expression == null) {
            val declaredType = getDeclaredType(typeDef!!, context)
            context.values[name] = Value(WilesUndefined, declaredType, variableStatus)
            return
        }

        if (!valueAlreadyKnown) {
            val isConst = details.contains(CONST_ID)

            var declaredType : AbstractType? = null
            val isLevelScoped = if(details.contains(LEVEL_SCOPE_ID)) {
                if(typeDef == null)
                    throw InferenceFailureException(nameToken.getFirstLocation())
                !isCheckingLevelScope
            } else false

            if((context.isCompiling || isLevelScoped) && typeDef != null)
            {
                declaredType = getDeclaredType(typeDef, context)
            }

            val processor = Processor(expression, newContext)
            val newValue = if (isLevelScoped) {
                Value(WilesLazyObject(processor), declaredType!!, VariableStatus.Const)
            } else {
                processor.process()
                val computedValue = processor.value
                val isVariable = variableStatus == VariableStatus.Var
                val newType = computedValue.getType()
                if (declaredType != null) {
                    if (!isSuperType(declaredType, newType))
                        throw TypeConflictError(declaredType, newType, typeDef!!.getFirstLocation())
                }
                if (context.isCompiling && isConst && !computedValue.isKnown())
                    throw ValueNotConstException(nameToken.getFirstLocation())
                val newTypeVague = if(context.isCompiling) newType.removeExact() else newType
                Value(computedValue.getObj(),
                    if (isVariable) declaredType?:newTypeVague else newType,
                    variableStatus)
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
        return context.isCompiling && context.values[name]?.isLazy() == true
    }

    private fun createContext() : InterpreterContext
    {
        val tempValues = ValuesMap()
        for((key,value) in context.values.entries)
        {
            val newValue = Value(null, value.getType().removeExact(), VariableStatus.Const)
            tempValues[key] = if(value.isLazy()) newValue else value
        }
        return InterpreterContext(context.isRunning, tempValues, context.isDebug, context.exceptions)
    }

}