package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.functions.WilesFunction
import wiles.processor.types.FunctionType
import wiles.processor.types.WilesType

class ApplyOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        val expectedType = calculateType()
        val expectedValues = expectedType.getSubtypes().map { it.exactValue }.distinct()
        if(expectedValues.size == 1 && expectedValues[0] != null)
            return Value(expectedValues[0], expectedType)
        return calculateObject() ?: Value(null, expectedType)
    }

    override fun calculateObject(): Value? {
        //TODO: multiple params
        assert(leftObj is WilesFunction)
        val func = leftObj as WilesFunction
        val result = func.invoke(ValuesMap(), context)
        return if(context.isCompiling && !func.pure) null
        else result
    }

    override fun calculateType(): WilesType {
        assert(leftType?.getSubtypes()?.all { it is FunctionType } == true)
        val subtypes = leftType!!.getSubtypes().map { (it as FunctionType).yieldsType }
        val allTypes = subtypes.map { it.getSubtypes() }.flatten().distinct().toTypedArray()
        return WilesType(*allTypes)
    }
}