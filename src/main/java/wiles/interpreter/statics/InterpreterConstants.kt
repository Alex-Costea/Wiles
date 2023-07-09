package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.shared.constants.TypeConstants
import java.math.BigDecimal
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

    operator fun BigInteger.div(value: Double) : Double
    {
        return this.toBigDecimal().div(value.toBigDecimal()).toDouble()
    }

    operator fun BigDecimal.div(value: Double) : Double
    {
        return this.div(value.toBigDecimal()).toDouble()
    }
}

