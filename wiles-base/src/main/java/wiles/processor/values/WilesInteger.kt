package wiles.processor.values

import org.apache.commons.math3.fraction.BigFraction
import java.math.BigInteger

class WilesInteger(private val value : BigInteger) {

    constructor(value : String) : this(BigInteger(value))
    constructor(value : Long) : this(BigInteger.valueOf(value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WilesInteger

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }

    operator fun plus(secondValue : WilesInteger) : WilesInteger
    {
        return WilesInteger(this.value + secondValue.value)
    }

    operator fun plus(secondValue : WilesDecimal) : WilesDecimal
    {
        return secondValue + this
    }

    operator fun minus(rightObj: WilesInteger): WilesInteger {
        return WilesInteger(this.value - rightObj.value)
    }

    operator fun minus(rightObj: WilesDecimal): WilesDecimal {
        return -rightObj + this
    }

    operator fun unaryMinus(): WilesInteger {
        return WilesInteger(-value)
    }

    operator fun unaryPlus(): WilesInteger {
        return this
    }

    operator fun times(rightObj: WilesInteger): WilesInteger {
        return WilesInteger(this.value * rightObj.value)
    }

    operator fun times(rightObj: WilesDecimal): WilesDecimal {
        return rightObj * this
    }

    operator fun div(rightObj: WilesInteger): WilesInteger {
        return WilesInteger(this.value / rightObj.value)
    }

    operator fun div(rightObj: WilesDecimal): WilesDecimal {
        return DECIMAL_ONE / rightObj * this
    }

    infix fun pow(rightObj: WilesInteger): WilesInteger {
        return WilesInteger(this.value.pow(rightObj.value.intValueExact()))
    }

    infix fun pow(rightObj: WilesDecimal): WilesDecimal {
        return WilesDecimal(this.toString()) pow rightObj
    }

    companion object{
        val DECIMAL_ONE = WilesDecimal(BigFraction.ONE)
    }

}