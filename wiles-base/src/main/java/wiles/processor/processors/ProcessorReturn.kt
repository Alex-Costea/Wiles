package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.YieldPossibility
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorReturn(syntax: AbstractSyntaxTree, context: InterpreterContext)
    : ProcessorExpression(syntax, context)
{
    override fun process() : Value{
        val processor = Processor(syntax.getComponents()[0], context)
        val newValue = processor.process()
        val location = syntax.getFirstLocation()
        context.yieldPossibilities.add(YieldPossibility(newValue.getType(), location))
        return newValue
    }
}