package wiles.processor.values

import org.apache.commons.math3.fraction.BigFraction
import java.math.BigDecimal
import java.math.BigInteger


class WilesDecimal(private val value : BigFraction)  {


    companion object{
        private fun stringToValue(value : String) : BigFraction{
            if(value.startsWith("-"))
                return stringToValue(value.substring(1)).multiply(-1)
            if(!value.contains("."))
                return stringToValue("$value.0")
            val (part1, part2) = value.split(".")
            val numerator = BigInteger(part1 + part2)
            val denominator = BigInteger("1" + "0".repeat(part2.length))
            return BigFraction(numerator, denominator)
        }
    }

    constructor(value : String) : this(stringToValue(value))


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if(other is WilesDecimal)
            return value.compareTo(other.value) == 0
        return false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    operator fun plus(secondValue : WilesDecimal) : WilesDecimal
    {
        return WilesDecimal((this.value.add(secondValue.value)))
    }

    operator fun plus(secondValue : WilesInteger) : WilesDecimal
    {
        return this + WilesDecimal(secondValue.toString())
    }

    override fun toString(): String {
        @Suppress("DEPRECATION")
        return value.bigDecimalValue(16, BigDecimal.ROUND_HALF_UP).toString()
    }

    operator fun unaryMinus(): WilesDecimal {
        return WilesDecimal(value.multiply(-1))
    }

    operator fun minus(rightObj: WilesInteger): WilesDecimal {
        return -rightObj + this
    }

    operator fun minus(rightObj: WilesDecimal): WilesDecimal {
        return WilesDecimal(this.value.subtract(rightObj.value))
    }

    operator fun unaryPlus(): WilesDecimal {
        return this
    }

    operator fun times(wilesInteger: WilesInteger): WilesDecimal {
        return this * WilesDecimal(wilesInteger.toString())
    }

    operator fun times(wilesDecimal: WilesDecimal): WilesDecimal {
        return WilesDecimal(this.value.multiply(wilesDecimal.value))
    }

    operator fun div(rightObj: WilesDecimal): WilesDecimal {
        return WilesDecimal(this.value.divide(rightObj.value))
    }

    operator fun div(wilesInteger: WilesInteger): WilesDecimal {
        return this / WilesDecimal(wilesInteger.toString())
    }

    infix fun pow(rightObj: WilesInteger): WilesDecimal {
        return this pow WilesDecimal(rightObj.toString())
    }

    infix fun pow(rightObj: WilesDecimal): WilesDecimal {
        val value = this.value.pow(rightObj.value.toDouble())
        if(value.isNaN())
            throw ArithmeticException()
        return WilesDecimal(value.toString())
    }

    operator fun compareTo(wilesDecimal: WilesDecimal): Int {
        return this.value.compareTo(wilesDecimal.value)
    }

}