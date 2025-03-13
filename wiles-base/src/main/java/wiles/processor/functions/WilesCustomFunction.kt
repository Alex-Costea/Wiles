package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.shared.abstracts.AbstractSyntaxTree

class WilesCustomFunction(
    private val capturedValues : ValuesMap,
    private val syntaxTree: AbstractSyntaxTree
) : WilesFunction() {
    override fun invoke(newValues : ValuesMap, context: InterpreterContext): Any {
        if(newValues.isNotEmpty()) TODO("parameters")
        TODO("WilesCustomFunction")
    }
}