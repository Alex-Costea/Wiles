package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.processor.processors.ProcessorCodeBlock
import wiles.shared.abstracts.AbstractSyntaxTree

class WilesCustomFunction(
    private val capturedValues : ValuesMap,
    private val syntaxTree: AbstractSyntaxTree
) : WilesFunction() {
    override val pure = false
    override fun invoke(newValues : ValuesMap, context: InterpreterContext): Any {
        val internalValues = ValuesMap(capturedValues)
        internalValues.putAll(newValues)
        val mergedContext = InterpreterContext(internalValues, context.isRunning, context.isDebug, context.exceptions)
        val processor = ProcessorCodeBlock(syntaxTree, mergedContext)
        val returnValue = processor.process()
        for((key,value) in internalValues)
        {
            if(capturedValues.containsKey(key))
                capturedValues[key] = value
        }
        return returnValue
    }
}