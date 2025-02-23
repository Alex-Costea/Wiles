package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.shared.AbstractSyntaxTree

class ProcessorTypeExpression(syntax: AbstractSyntaxTree, context: InterpreterContext)
    : ProcessorExpression(syntax, context)
{
    override fun process() {
        if(syntax.components.size > 1)
            super.process()
        else{
            val processorExpression = ProcessorExpression(syntax.components[0], context)
            processorExpression.process()
            value = processorExpression.value
        }
        //TODO: process value. e.g: 2 is exact type of 2
    }
}