package `in`.costea.wiles.constants

import `in`.costea.wiles.constants.Tokens.ACCESS_ID
import `in`.costea.wiles.constants.Tokens.AND_ID
import `in`.costea.wiles.constants.Tokens.APPLY_ID
import `in`.costea.wiles.constants.Tokens.DIVIDE_ID
import `in`.costea.wiles.constants.Tokens.ELEM_ID
import `in`.costea.wiles.constants.Tokens.EQUALS_ID
import `in`.costea.wiles.constants.Tokens.LARGER_EQUALS_ID
import `in`.costea.wiles.constants.Tokens.LARGER_ID
import `in`.costea.wiles.constants.Tokens.MINUS_ID
import `in`.costea.wiles.constants.Tokens.NOT_EQUAL_ID
import `in`.costea.wiles.constants.Tokens.NOT_ID
import `in`.costea.wiles.constants.Tokens.OR_ID
import `in`.costea.wiles.constants.Tokens.PLUS_ID
import `in`.costea.wiles.constants.Tokens.POWER_ID
import `in`.costea.wiles.constants.Tokens.SMALLER_EQUALS_ID
import `in`.costea.wiles.constants.Tokens.SMALLER_ID
import `in`.costea.wiles.constants.Tokens.TIMES_ID
import `in`.costea.wiles.constants.Tokens.UNARY_MINUS_ID
import `in`.costea.wiles.constants.Tokens.UNARY_PLUS_ID
import java.util.HashMap

object Precedence {

    val PRECEDENCE : HashMap<String, Byte> = HashMap()
    val RIGHT_TO_LEFT : Set<Byte>

    init {
        PRECEDENCE[OR_ID] = -4
        PRECEDENCE[AND_ID] = -3
        PRECEDENCE[NOT_ID] = -2
        PRECEDENCE[EQUALS_ID] = -1
        PRECEDENCE[NOT_EQUAL_ID] = -1
        PRECEDENCE[LARGER_ID] = 0
        PRECEDENCE[SMALLER_ID] = 0
        PRECEDENCE[LARGER_EQUALS_ID] = 0
        PRECEDENCE[SMALLER_EQUALS_ID] = 0
        PRECEDENCE[PLUS_ID] = 1
        PRECEDENCE[MINUS_ID] = 1
        PRECEDENCE[UNARY_PLUS_ID] = 2
        PRECEDENCE[UNARY_MINUS_ID] = 2
        PRECEDENCE[TIMES_ID] = 3
        PRECEDENCE[DIVIDE_ID] = 3
        PRECEDENCE[POWER_ID] = 4
        PRECEDENCE[ELEM_ID] = 5
        PRECEDENCE[ACCESS_ID] = 6
        PRECEDENCE[APPLY_ID] = 6

        RIGHT_TO_LEFT = setOf(PRECEDENCE[NOT_ID]!!, PRECEDENCE[UNARY_PLUS_ID]!!, PRECEDENCE[POWER_ID]!!)
    }
}