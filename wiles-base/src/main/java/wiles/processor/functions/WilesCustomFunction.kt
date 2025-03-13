package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.processor.processors.ProcessorCodeBlock
import wiles.shared.abstracts.AbstractSyntaxTree

class WilesCustomFunction(
    private val capturedValues : ValuesMap,
    private val syntaxTree: AbstractSyntaxTree
) : WilesFunction() {
    override fun invoke(newValues : ValuesMap, context: InterpreterContext): Any {
        if(newValues.isNotEmpty()) TODO("parameters")
        val mergedContext = InterpreterContext(capturedValues, context.isRunning, context.isDebug, context.exceptions)
        val processor = ProcessorCodeBlock(syntaxTree, mergedContext)
        return processor.process()
    }
}