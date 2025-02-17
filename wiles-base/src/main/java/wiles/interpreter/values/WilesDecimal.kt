package wiles.interpreter.values

import java.math.BigDecimal
import java.math.MathContext

class WilesDecimal(value : String)  {

    private val decimal = BigDecimal(value, MathContext.DECIMAL128)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if(other is WilesDecimal)
            return decimal.compareTo(other.decimal) == 0
        return false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}