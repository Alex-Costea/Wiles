package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueData
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.InferenceFailureException
import wiles.processor.errors.TypeConflictError
import wiles.processor.errors.ValueNotConstException
import wiles.processor.functions.WilesFunction
import wiles.processor.processors.Processor.Companion.NOTHING_VALUE
import wiles.processor.types.WilesType
import wiles.processor.utils.InterpreterUtils
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesLazyObject
import wiles.processor.values.WilesUndefined
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.InternalErrorException

class ProcessorDeclaration(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext,
    private val forceLevelScope : Boolean = false
) : AbstractProcessor(syntax, context) {
    override fun process(): Value {
        val components = syntax.getComponents().toMutableList()
        val typeDef = if(components[0].syntaxType == SyntaxType.TYPEDEF) components.removeAt(0) else null
        val nameToken = components[0]
        val name = nameToken.details[0]
        val expression = components.getOrNull(1)
        val details = syntax.details
        val isLevelScoped = forceLevelScope || details.contains(LEVEL_SCOPE_ID)
        val shouldCheckLevelScoped = isLevelScoped && context.isCompiling && context.values.containsKey(name)
        val newContext = if(shouldCheckLevelScoped) createContext() else context
        val valueAlreadyKnown = newContext.values[name]?.value?.isKnown() == true
        val isConst = details.contains(CONST_ID)
        val isVariable = details.contains(VARIABLE_ID)
        val isAnonArg = details.contains(ANON_ARG_ID)
        val variableStatus = determineVariableStatus(isVariable, isAnonArg, isConst)

        if(newContext.isCompiling && newContext.values.containsKey(name) && !shouldCheckLevelScoped)
        {
            throw IdentifierAlreadyDeclaredException(nameToken.getFirstLocation())
        }

        if(expression == null) {
            val declaredType = getDeclaredType(name, typeDef)
            context.values[name] = ValueData(Value(WilesUndefined,
                declaredType ?: throw InternalErrorException()), variableStatus)
            return NOTHING_VALUE
        }

        if (!valueAlreadyKnown) {
            val shouldInitForLevelScoped = if(isLevelScoped) !shouldCheckLevelScoped else false

            val declaredType : WilesType? = if(shouldInitForLevelScoped)
            {
                getDeclaredTypeForLevelScope(typeDef, nameToken, expression, newContext)
            }
            else getDeclaredType(name, typeDef)

            val processor = Processor(expression, newContext)
            val newValue = if (shouldInitForLevelScoped) {
                ValueData(Value(WilesLazyObject(processor), declaredType!!), VariableStatus.Val)
            } else {
                val computedValue = processor.process()
                val newType = computedValue.getType()
                if (context.isCompiling && declaredType != null) {
                    if (!isSuperType(declaredType, newType))
                        throw TypeConflictError(declaredType, newType, typeDef!!.getFirstLocation())
                }
                if (context.isCompiling && isConst)
                {
                    if(!computedValue.isKnown())
                        throw ValueNotConstException(nameToken.getFirstLocation())
                    val obj = computedValue.getObj()
                    if(obj is WilesFunction && !obj.pure)
                        throw ValueNotConstException(nameToken.getFirstLocation())
                }
                val vagueNewType = if (context.isCompiling) newType.removeExact() else newType
                val newDeclaredType = if (variableStatus == VariableStatus.Var || context.isRunning) {
                    declaredType ?: vagueNewType
                } else newType
                ValueData(Value(computedValue.getObj(), newType), variableStatus, newDeclaredType)
            }
            context.values[name] = newValue
        }
        return NOTHING_VALUE
    }

    private fun getDeclaredTypeForLevelScope(
        typeDef: AbstractSyntaxTree?,
        nameToken: AbstractSyntaxTree,
        expression: AbstractSyntaxTree,
        newContext: InterpreterContext
    ) : WilesType?
    {
        if(typeDef != null)
            return getDeclaredType(nameToken.details[0], typeDef)
        //TODO: infer from function
        if(expression.syntaxType != SyntaxType.TOKEN)
            throw InferenceFailureException(nameToken.getFirstLocation())
        if(IS_IDENTIFIER.test(expression.details[0]))
            throw InferenceFailureException(nameToken.getFirstLocation())
        val processor = ProcessorToken(expression, newContext)
        return processor.process().getType()
    }

    private fun getDeclaredType(name : String, typeDef : AbstractSyntaxTree?): WilesType? {
        if(context.isRunning)
            return context.values[name]?.comptimeType

        typeDef ?: return null
        return InterpreterUtils.processType(typeDef, context)
    }

    private fun createContext() : InterpreterContext
    {
        val tempValues = ValuesMap()
        for((key,value) in context.values.entries)
        {
            val newValue = Value(null, value.value.getType().removeExact())
            tempValues[key] = if(value.value.isLazy()) ValueData(newValue, VariableStatus.Val) else value
        }
        return InterpreterContext(tempValues, context.isRunning, context.exceptions, context.yieldPossibilities)
    }

    private fun determineVariableStatus(isVariable: Boolean, isAnonArg: Boolean, isConst: Boolean): VariableStatus {
        return when {
            isVariable -> VariableStatus.Var
            isAnonArg && isConst -> VariableStatus.ComptimeArg
            isAnonArg -> VariableStatus.Arg
            isConst -> VariableStatus.Comptime
            else -> VariableStatus.Val
        }
    }


}