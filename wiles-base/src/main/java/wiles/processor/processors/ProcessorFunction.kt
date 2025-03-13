package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorFunction(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {
    override fun process(): Value {
        TODO("ProcessorFunction")
    }

}