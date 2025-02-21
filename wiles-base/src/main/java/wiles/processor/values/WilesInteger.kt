package wiles.processor.values

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

}