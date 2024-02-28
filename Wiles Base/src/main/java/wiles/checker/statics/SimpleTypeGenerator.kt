package wiles.checker.statics

import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.TypeConstants.AND_OPERATION
import wiles.shared.constants.TypeConstants.ASSIGN_OPERATION
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.DIVIDE_OPERATION
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.INT_TYPE
import wiles.shared.constants.TypeConstants.LARGER_EQUALS_OPERATION
import wiles.shared.constants.TypeConstants.LARGER_OPERATION
import wiles.shared.constants.TypeConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
import wiles.shared.constants.TypeConstants.MINUS_OPERATION
import wiles.shared.constants.TypeConstants.MUTABLE_OPERATION
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.NOT_OPERATION
import wiles.shared.constants.TypeConstants.OR_OPERATION
import wiles.shared.constants.TypeConstants.PLUS_OPERATION
import wiles.shared.constants.TypeConstants.POWER_OPERATION
import wiles.shared.constants.TypeConstants.SMALLER_EQUALS_OPERATION
import wiles.shared.constants.TypeConstants.SMALLER_OPERATION
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.TIMES_OPERATION
import wiles.shared.constants.TypeConstants.UNARY_MINUS_OPERATION
import wiles.shared.constants.TypeConstants.UNARY_PLUS_OPERATION
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.TypeUtils.makeTypeUngeneric

object SimpleTypeGenerator {

    private val simpleTypes = mapOf(
        //Addition
        Pair(Triple(INT_TYPE, PLUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT_TYPE, PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, INT_TYPE), DOUBLE_TYPE),

        //Subtraction
        Pair(Triple(INT_TYPE, MINUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DOUBLE_TYPE, MINUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT_TYPE, MINUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, MINUS_OPERATION, INT_TYPE), DOUBLE_TYPE),

        //Multiplication
        Pair(Triple(INT_TYPE, TIMES_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DOUBLE_TYPE, TIMES_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT_TYPE, TIMES_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, TIMES_OPERATION, INT_TYPE), DOUBLE_TYPE),

        //Division
        Pair(Triple(INT_TYPE, DIVIDE_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DOUBLE_TYPE, DIVIDE_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT_TYPE, DIVIDE_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, DIVIDE_OPERATION, INT_TYPE), DOUBLE_TYPE),

        //Exponentiation
        Pair(Triple(INT_TYPE, POWER_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DOUBLE_TYPE, POWER_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT_TYPE, POWER_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, POWER_OPERATION, INT_TYPE), DOUBLE_TYPE),

        //Prefix plus/minus
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_MINUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_MINUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),

        //Boolean operations
        Pair(Triple(BOOLEAN_TYPE, AND_OPERATION, BOOLEAN_TYPE), BOOLEAN_TYPE),
        Pair(Triple(BOOLEAN_TYPE, OR_OPERATION, BOOLEAN_TYPE), BOOLEAN_TYPE),
        Pair(Triple(NOTHING_TYPE, NOT_OPERATION, BOOLEAN_TYPE), BOOLEAN_TYPE),

        //String concatenation
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),

        //String and boolean concatenation
        Pair(Triple(BOOLEAN_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, BOOLEAN_TYPE), STRING_TYPE),

        //String and int concatenation
        Pair(Triple(INT_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, INT_TYPE), STRING_TYPE),

        //String and double concatenation
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, DOUBLE_TYPE), STRING_TYPE),

        //Larger
        Pair(Triple(INT_TYPE, LARGER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, LARGER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Larger Equals
        Pair(Triple(INT_TYPE, LARGER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, LARGER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Smaller
        Pair(Triple(INT_TYPE, SMALLER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, SMALLER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Smaller Equals
        Pair(Triple(INT_TYPE, SMALLER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, SMALLER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Repeat string
        Pair(Triple(STRING_TYPE, TIMES_OPERATION, INT_TYPE), STRING_TYPE),
        Pair(Triple(INT_TYPE, TIMES_OPERATION, STRING_TYPE), STRING_TYPE),
        )

    fun getSimpleTypes(triple : Triple<JSONStatement, JSONStatement, JSONStatement>) : JSONStatement?
    {
        val unboxedTriple = Triple(makeTypeUngeneric(triple.first), triple.second, makeTypeUngeneric(triple.third))

        if((unboxedTriple.second.name == EQUALS_ID || unboxedTriple.second.name == NOT_EQUAL_ID)
                   && (isFormerSuperTypeOfLatter(NOTHING_TYPE, unboxedTriple.first)
                    || isFormerSuperTypeOfLatter(NOTHING_TYPE, unboxedTriple.third)
                    || isFormerSuperTypeOfLatter(unboxedTriple.first,unboxedTriple.third)
                    || isFormerSuperTypeOfLatter(unboxedTriple.third,unboxedTriple.first)))
            return BOOLEAN_TYPE

        if(triple.second.name == PLUS_ID
            && isFormerSuperTypeOfLatter(LIST_OF_NULLABLE_ANYTHING_TYPE, triple.first)
            && isFormerSuperTypeOfLatter(triple.first, triple.third))
                return triple.first.copyRemovingLocation()

        if(unboxedTriple.second == MUTABLE_OPERATION) {
            assert(unboxedTriple.first == NOTHING_TYPE)
            return makeMutable(unboxedTriple.third)
        }

        if(unboxedTriple.second == ASSIGN_OPERATION && isFormerSuperTypeOfLatter(unboxedTriple.first,unboxedTriple.third))
        {
            return NOTHING_TYPE
        }

        return simpleTypes[unboxedTriple]
    }
}