package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.types.NothingType
import wiles.processor.values.Value
import wiles.shared.AbstractSyntaxTree

class AssignmentOperation(left: Value, right: Value, context: InterpreterContext,
                          private val leftToken : AbstractSyntaxTree)
    : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        val name = leftToken.details[0]
        val location = leftToken.location!!
        val leftValue = context.values[name]!!
        if(!leftValue.isVariable()) throw CantBeModifiedException(location)
        //TODO: check if type checking works
        val newValue = Value(right.getObj(), right.getType().clone(), true)
        context.values[name] = newValue
        return Value(null, NothingType(), false)
    }
}