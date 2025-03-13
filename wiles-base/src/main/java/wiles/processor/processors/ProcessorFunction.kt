package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.functions.WilesCustomFunction
import wiles.processor.types.AbstractType
import wiles.processor.types.FunctionType
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.enums.SyntaxType

class ProcessorFunction(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {
    override fun process(): Value {
        val components = syntax.getComponents().toMutableList()
        val typeDef = if(components[0].syntaxType == SyntaxType.TYPEDEF) components.removeAt(0) else null
        if(typeDef != null) TODO("type definition")
        val codeBlock = if(components[components.size-1].syntaxType == SyntaxType.CODE_BLOCK)
            components.removeAt(components.size-1) else null
        codeBlock ?: TODO("no code block")
        if(components.size > 0) TODO("function declarations")
        val newContext = getNewContext()
        if(context.isCompiling)
        {
            val processor = ProcessorCodeBlock(codeBlock, newContext)
            processor.process()
        }
        return Value(VariableStatus.Const,
            WilesCustomFunction(newContext.values, codeBlock),
            FunctionType(null, AbstractType.NOTHING_TYPE))
    }

    private fun getNewContext(): InterpreterContext {
        if(context.isRunning)
            return context
        val newValues = ValuesMap()
        for((name, value) in context.values)
        {
            val variableStatus = if(value.isVariable()) VariableStatus.Var else VariableStatus.Const
            if(value.isKnown() && !value.isVariable())
                newValues[name] = Value(variableStatus, value.getObj(), value.getType(), value.getComptimeType())
            else newValues[name] = Value(variableStatus, null, value.getComptimeType())
        }
        return InterpreterContext(newValues, false, context.isDebug, context.exceptions)
    }

}