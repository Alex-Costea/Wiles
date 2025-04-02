package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueData
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.TypeConflictError
import wiles.processor.functions.WilesCustomFunction
import wiles.processor.types.FunctionType
import wiles.processor.types.WilesType
import wiles.processor.utils.InterpreterUtils.filterOutImpure
import wiles.processor.utils.InterpreterUtils.getYieldedType
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesUndefined
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Tokens.PURE_ID
import wiles.shared.enums.SyntaxType

class ProcessorFunction(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {
    override fun process(): Value {
        val components = syntax.getComponents().toMutableList()
        val yieldStatement = if(components[0].syntaxType == SyntaxType.TYPEDEF) components.removeAt(0) else null
        val codeBlock = if(components[components.size-1].syntaxType == SyntaxType.CODE_BLOCK)
            components.removeAt(components.size-1) else null
        codeBlock ?: TODO("no code block -> is type expression")
        val isDeclaredPure = syntax.details.contains(PURE_ID)
        val newContext = getNewContext(isDeclaredPure)

        //process parameters
        val oldValues = ValuesMap(newContext.values)
        for(component in components) // type check without context
        {
            assert(component.syntaxType == SyntaxType.DECLARATION)
            val paramProcessor = ProcessorDeclaration(component, newContext, forceLevelScope = true)
            paramProcessor.process()
        }
        for(component in components) //actually figure out values
        {
            val paramProcessor = ProcessorDeclaration(component, newContext, forceLevelScope = true)
            paramProcessor.process()
        }
        val paramValues = newContext.values.filter { !oldValues.containsKey(it.key) }

        for(key in paramValues.keys)
        {
            val valueData = newContext.values[key]
            if(valueData?.value?.getObj() == WilesUndefined)
            {
                val value = valueData.value
                newContext.values[key] = ValueData(Value(null, value.getType()),valueData.variableStatus)
            }
        }

        val processorCodeBlock = ProcessorCodeBlock(codeBlock, newContext)
        processorCodeBlock.process()
        val yieldedType = getYieldedType(newContext.yieldPossibilities)

        if(context.isCompiling && yieldStatement != null)
        {
            val definedType = ProcessorTypeExpression(yieldStatement, newContext).process().getObj()
            assert(definedType is WilesType)
            if(!isSuperType(definedType as WilesType, yieldedType))
                throw TypeConflictError(definedType, yieldedType, yieldStatement.getFirstLocation())
        }
        val newFunction = WilesCustomFunction(context.values, codeBlock, isDeclaredPure)
        return Value(newFunction, WilesType(FunctionType(paramValues, yieldedType)))
    }

    private fun getNewContext(pure : Boolean): InterpreterContext {
        if(context.isRunning)
            return context
        val newValues = ValuesMap()
        for((name, valueData) in context.values)
        {
            val value = valueData.value
            val variableStatus = if(valueData.isVariable()) VariableStatus.Var else VariableStatus.Val
            if(value.isKnown() && !valueData.isVariable())
                newValues[name] = ValueData(Value(value.getObj(), value.getType()),
                    variableStatus, valueData.comptimeType)
            else newValues[name] = ValueData(Value(null, valueData.comptimeType), variableStatus)
        }
        return InterpreterContext(filterOutImpure(newValues, pure), false, context.exceptions, mutableListOf())
    }

}