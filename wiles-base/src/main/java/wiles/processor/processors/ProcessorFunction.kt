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
        if(components.size > 0) TODO("function declarations")
        //TODO: type definitions and parameters should be analysed as level scope
        val isDeclaredPure = syntax.details.contains(PURE_ID)
        val newContext = getNewContext(isDeclaredPure)
        val processor = ProcessorCodeBlock(codeBlock, newContext)
        processor.process()
        val yieldedType = getYieldedType(newContext.yieldPossibilities)
        if(context.isCompiling && yieldStatement != null)
        {
            val definedType = ProcessorTypeExpression(yieldStatement, newContext).process().getObj()
            assert(definedType is WilesType)
            if(!isSuperType(definedType as WilesType, yieldedType))
                throw TypeConflictError(definedType, yieldedType, yieldStatement.getFirstLocation())
        }
        val newFunction = WilesCustomFunction(context.values, codeBlock, isDeclaredPure)
        return Value(newFunction, WilesType(FunctionType(null, ValuesMap(), yieldedType)))
    }

    private fun getNewContext(pure : Boolean): InterpreterContext {
        if(context.isRunning)
            return context
        val newValues = ValuesMap()
        for((name, valueData) in context.values)
        {
            val value = valueData.value
            val variableStatus = if(valueData.isVariable()) VariableStatus.Var else VariableStatus.Const
            if(value.isKnown() && !valueData.isVariable())
                newValues[name] = ValueData(Value(value.getObj(), value.getType()),
                    variableStatus, valueData.getComptimeType())
            else newValues[name] = ValueData(Value(null, valueData.getComptimeType()), variableStatus)
        }
        return InterpreterContext(filterOutImpure(newValues, pure), false, context.exceptions, mutableListOf())
    }

}