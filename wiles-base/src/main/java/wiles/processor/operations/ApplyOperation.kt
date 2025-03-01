package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps
import wiles.processor.data.ValuesMap
import wiles.processor.functions.WilesFunction
import wiles.processor.types.AbstractType
import wiles.processor.types.FunctionType

class ApplyOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        //TODO: check if calculable at compile time
        return if (context.compileMode) {
            Value(null, calculateType(), ValueProps.DEFAULT_EXPR)
        } else {
            val obj = calculateObject()
            Value(obj, calculateType().exactly(obj), ValueProps.DEFAULT_EXPR)
        }
    }

    override fun calculateObject(): Any {
        assert(leftObj is WilesFunction)
        return (leftObj as WilesFunction).invoke(ValuesMap())
    }

    override fun calculateType(): AbstractType {
        assert(leftType is FunctionType)
        return (leftType as FunctionType).yieldsType
    }
}