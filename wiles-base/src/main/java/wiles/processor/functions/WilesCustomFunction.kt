package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.processor.processors.ProcessorCodeBlock
import wiles.processor.utils.TypeUtils.filterOutImpure
import wiles.shared.abstracts.AbstractSyntaxTree

class WilesCustomFunction(
    private val capturedValues : ValuesMap,
    private val syntaxTree: AbstractSyntaxTree,
    override val pure : Boolean
) : WilesFunction() {

    override fun invoke(newValues : ValuesMap, context: InterpreterContext): Any {
        val internalValues = filterOutImpure(capturedValues, pure)
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