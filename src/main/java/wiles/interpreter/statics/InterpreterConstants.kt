package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.ErrorMessages.CANNOT_PERFORM_OPERATION_ERROR
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.TypeConstants
import java.math.BigInteger

object InterpreterConstants {

    val ZERO_REF = ObjectDetails(BigInteger.ZERO, TypeConstants.INT_TYPE)
    //TODO: should be infinity?
    val MAX_INT_REF = ObjectDetails(Long.MAX_VALUE.toBigInteger(), TypeConstants.INT_TYPE)

    fun BigInteger.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

    operator fun BigInteger.compareTo(value: Int): Int {
        return this.compareTo(value.toBigInteger())
    }
    operator fun BigInteger.compareTo(value: Double): Int {
        return this.toBigDecimal().compareTo(value.toBigDecimal())
    }

    operator fun BigInteger.plus(value: Double) : Double
    {
        return this.toBigDecimal().plus(value.toBigDecimal()).toDouble()
    }

    operator fun BigInteger.minus(value: Double) : Double
    {
        return this.toBigDecimal().minus(value.toBigDecimal()).toDouble()
    }

    operator fun BigInteger.times(value: Double) : Double
    {
        return this.toBigDecimal().times(value.toBigDecimal()).toDouble()
    }

    fun BigInteger.pow(value: BigInteger): Any? {
        val newValue = value.toIntOrNull() ?: throw PanicException(
            CANNOT_PERFORM_OPERATION_ERROR.format(this,POWER_ID,value))
        return this.pow(newValue)
    }
}

