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
        val obj = calculateObject()
        //TODO: check if calculable at compile time
        return if(context.isCompiling)
            Value(VariableStatus.Const, obj, calculateType())
        else Value(VariableStatus.Const, obj, calculateType().exactly(obj))
    }

    override fun calculateObject(): Any? {
        //TODO: multiple params
        assert(leftObj is WilesFunction)
        val func = leftObj as WilesFunction
        val result = func.invoke(ValuesMap(), context)
        return if(context.isCompiling && !func.pure)
            null
        else result
    }

    override fun calculateType(): AbstractType {
        //TODO: handle either function types
        assert(leftType is FunctionType)
        return (leftType as FunctionType).yieldsType
    }
}