package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.ErrorMessages.CANNOT_PERFORM_OPERATION_ERROR
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.TypeConstants
import java.math.BigInteger

object InterpreterConstants {

    val ZERO_REF = ObjectDetails(BigInteger.ZERO, TypeConstants.INT_TYPE)

    fun BigInteger.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

    operator fun BigInteger.compareTo(value: Int): Int {
        return this.compareTo(value.toBigInteger())
    }
    operator fun BigInteger.compareTo(value: Double): Int {
        return when (value) {
            Double.POSITIVE_INFINITY -> -1
            Double.NEGATIVE_INFINITY -> 1
            else -> this.toDouble().compareTo(value)
        }
    }

    operator fun BigInteger.plus(value: Double) : Double
    {
        return this.toDouble().plus(value)
    }

    operator fun BigInteger.minus(value: Double) : Double
    {
        return this.toDouble().minus(value)
    }

    operator fun BigInteger.times(value: Double) : Double
    {
        return this.toDouble().times(value)
    }

    fun BigInteger.pow(value: BigInteger): Any? {
        val newValue = value.toIntOrNull() ?: throw PanicException(
            CANNOT_PERFORM_OPERATION_ERROR.format(this,POWER_ID,value))
        return this.pow(newValue)
    }
}

