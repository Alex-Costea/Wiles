package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.shared.constants.TypeConstants

object InterpreterConstants {

    val ZERO_REF = ObjectDetails(0L, TypeConstants.INT_TYPE)
    val MAX_INT_REF = ObjectDetails(Long.MAX_VALUE, TypeConstants.INT_TYPE)

    fun Long.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

}