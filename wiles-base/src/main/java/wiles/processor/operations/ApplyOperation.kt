package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.functions.WilesFunction
import wiles.processor.types.FunctionType
import wiles.processor.types.WilesType
import wiles.shared.errors.InternalErrorException
import wiles.shared.errors.WilesTypeException

class ApplyOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        val expectedType = calculateType()
        val exactValue = expectedType.getExactIfExists()
        if(exactValue != null)
            return Value(exactValue, expectedType)
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
        leftType ?: throw InternalErrorException()
        if(!leftType.getSubtypes().all { it is FunctionType })
            throw WilesTypeException(leftType, rightType)
        val subtypes = leftType.getSubtypes().map { (it as FunctionType).yieldsType.getSubtypes() }.flatten()
        return WilesType(*subtypes.toTypedArray())
    }
}