package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.FALSE_TYPE
import wiles.processor.types.AbstractType.Companion.TRUE_TYPE
import wiles.processor.types.AbstractType.Companion.TRUTH_TYPE
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.shared.errors.WilesTypeException

class AndOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    var value : Any? = null
    override fun calculateObject(): Any? {
        if(!bothKnown)
            return null
        if(value != null)
            return value
        value = if(leftObj is Boolean && rightObj is Boolean)
            leftObj && rightObj
        else null
        return value
    }

    override fun calculateType(): AbstractType {
        return when {
            isSuperType(TRUTH_TYPE, leftType!!) && isSuperType(TRUTH_TYPE, rightType) -> {
                when (calculateObject()) {
                    true -> TRUE_TYPE
                    false -> FALSE_TYPE
                    else -> TRUTH_TYPE
                }
            }
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}