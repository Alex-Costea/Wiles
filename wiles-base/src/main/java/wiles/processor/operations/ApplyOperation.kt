package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.functions.WilesFunction
import wiles.processor.types.AbstractType
import wiles.processor.types.FunctionType

class ApplyOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        //TODO: check if calculable at compile time
        return if (context.isCompiling) {
            Value(VariableStatus.Const, null, calculateType(), )
        } else {
            val obj = calculateObject()
            Value(VariableStatus.Const, obj, calculateType().exactly(obj), )
        }
    }

    override fun calculateObject(): Any {
        assert(leftObj is WilesFunction)
        return (leftObj as WilesFunction).invoke(ValuesMap())
    }

    override fun calculateType(): AbstractType {
        //TODO: handle either function types
        assert(leftType is FunctionType)
        return (leftType as FunctionType).yieldsType
    }
}