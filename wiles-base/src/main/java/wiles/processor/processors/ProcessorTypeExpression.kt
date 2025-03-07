package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.ValueNotConstException
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.utils.TypeUtils.getNewTypeObject
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorTypeExpression(syntax: AbstractSyntaxTree, context: InterpreterContext)
    : ProcessorExpression(syntax, context)
{
    override fun process() : Value{
        var value : Value
        if(syntax.getComponents().size > 1) {
            return super.process()
        }
        else{
            val processor = Processor(syntax.getComponents()[0], context)
            processor.process()
            value = processor.process()
        }
        if(value.isKnown()) {
            val newValue = getNewTypeObject(value)
            value = Value(newValue, TYPE_TYPE, VariableStatus.Const)
        }
        else {
            throw ValueNotConstException(syntax.getFirstLocation())
        }
        return value
    }
}