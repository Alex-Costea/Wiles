package wiles.processor.values

import java.math.BigDecimal
import java.math.MathContext

class WilesDecimal(private val value : BigDecimal)  {

    constructor(value : String) : this(BigDecimal(value, MathContext.DECIMAL128))

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
        return WilesDecimal((this.value + secondValue.value).toString())
    }

    operator fun plus(secondValue : WilesInteger) : WilesDecimal
    {
        return this + WilesDecimal(secondValue.toString())
    }

    override fun toString(): String {
        return value.toString()
    }

    operator fun unaryMinus(): WilesDecimal {
        return WilesDecimal(-value)
    }

    operator fun minus(rightObj: WilesInteger): WilesDecimal {
        return -rightObj + this
    }

    operator fun minus(rightObj: WilesDecimal): WilesDecimal {
        return WilesDecimal(this.value - rightObj.value)
    }

    operator fun unaryPlus(): WilesDecimal {
        return this
    }

}