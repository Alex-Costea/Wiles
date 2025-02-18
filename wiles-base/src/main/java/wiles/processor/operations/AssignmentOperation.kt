package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.types.NothingType
import wiles.processor.values.Value

class AssignmentOperation(left: Value, right: Value, context: InterpreterContext,val name : String)
    : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        val leftValue = context.values.getOrDefault(name,  null) ?: TODO("Name doesn't exist")
        if(!leftValue.isVariable()) TODO("Is not variable")
        //TODO: check if type checking works
        val newValue = Value(right.getObj(), right.getType().clone(), true)
        context.values[name] = newValue
        return Value(null, NothingType(), false)
    }
}