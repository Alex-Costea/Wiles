package wiles.shared.constants

import wiles.shared.constants.Tokens.ACCESS_ID
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.AS_ID
import wiles.shared.constants.Tokens.AT_KEY_ID
import wiles.shared.constants.Tokens.DIVIDE_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.INTERNAL_ID
import wiles.shared.constants.Tokens.LARGER_EQUALS_ID
import wiles.shared.constants.Tokens.LARGER_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.MUTIFY_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.Tokens.RANGIFY_ID
import wiles.shared.constants.Tokens.SMALLER_EQUALS_ID
import wiles.shared.constants.Tokens.SMALLER_ID
import wiles.shared.constants.Tokens.SUBTYPES_ID
import wiles.shared.constants.Tokens.TIMES_ID
import wiles.shared.constants.Tokens.UNARY_MINUS_ID
import wiles.shared.constants.Tokens.UNARY_PLUS_ID
import wiles.shared.constants.Tokens.UNION_ID

object Precedence {

    val PRECEDENCE : HashMap<String, Byte> = HashMap()
    val RIGHT_TO_LEFT : Set<Byte>

    init {
        PRECEDENCE[OR_ID] = -7
        PRECEDENCE[AND_ID] = -6
        PRECEDENCE[NOT_ID] = -5
        PRECEDENCE[SUBTYPES_ID] = -4
        PRECEDENCE[UNION_ID] = -3
        PRECEDENCE[EQUALS_ID] = -2
        PRECEDENCE[NOT_EQUAL_ID] = -2
        PRECEDENCE[LARGER_ID] = -1
        PRECEDENCE[SMALLER_ID] = -1
        PRECEDENCE[LARGER_EQUALS_ID] = -1
        PRECEDENCE[SMALLER_EQUALS_ID] = -1
        PRECEDENCE[RANGIFY_ID] = -1
        PRECEDENCE[AS_ID] = -1
        PRECEDENCE[PLUS_ID] = 0
        PRECEDENCE[MINUS_ID] = 0
        PRECEDENCE[UNARY_PLUS_ID] = 1
        PRECEDENCE[UNARY_MINUS_ID] = 1
        PRECEDENCE[MUTIFY_ID] = 1
        PRECEDENCE[TIMES_ID] = 2
        PRECEDENCE[DIVIDE_ID] = 2
        PRECEDENCE[POWER_ID] = 3
        PRECEDENCE[MAYBE_ID] = 4
        PRECEDENCE[ACCESS_ID] = 5
        PRECEDENCE[AT_KEY_ID] = 6
        PRECEDENCE[APPLY_ID] = 6
        PRECEDENCE[INTERNAL_ID] = 7

        RIGHT_TO_LEFT = setOf(PRECEDENCE[NOT_ID]!!, PRECEDENCE[UNARY_PLUS_ID]!!,
            PRECEDENCE[POWER_ID]!!, PRECEDENCE[INTERNAL_ID]!!)
    }
}