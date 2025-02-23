package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps.Companion.DEFAULT_EXPR
import wiles.processor.data.ValueProps.Companion.VARIABLE_EXPR
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.errors.TypeConflictError
import wiles.processor.types.AbstractType
import wiles.processor.types.NothingType
import wiles.processor.utils.TypeUtils
import wiles.processor.values.Value
import wiles.processor.values.WilesNothing
import wiles.shared.AbstractSyntaxTree

class AssignmentOperation(left: Value, right: Value, context: InterpreterContext,
                          private val leftToken : AbstractSyntaxTree)
    : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        val name = leftToken.details[0]
        if(context.compileMode) {
            val location = leftToken.getFirstLocation()
            val leftValue = context.values[name]!!
            if (!leftValue.isVariable()) throw CantBeModifiedException(location)
            if (!TypeUtils.isSuperType(leftType!!, rightType))
                throw TypeConflictError(leftType, rightType, location)
        }
        val newValue = Value(right.getObj(), right.getType(), VARIABLE_EXPR)
        context.values[name] = newValue
        return Value(calculateObject(), calculateType(), DEFAULT_EXPR)
    }

    override fun calculateObject(): Any {
        return WilesNothing
    }

    override fun calculateType(): AbstractType {
        return NothingType()
    }
}