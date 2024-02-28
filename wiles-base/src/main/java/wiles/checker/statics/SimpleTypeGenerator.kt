package wiles.checker.statics

import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.TypeConstants.AND_OPERATION
import wiles.shared.constants.TypeConstants.ASSIGN_OPERATION
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.COLLECTION_OF_NULLABLE_ANYTHING
import wiles.shared.constants.TypeConstants.DIVIDE_OPERATION
import wiles.shared.constants.TypeConstants.DECIMAL_TYPE
import wiles.shared.constants.TypeConstants.INT_TYPE
import wiles.shared.constants.TypeConstants.LARGER_EQUALS_OPERATION
import wiles.shared.constants.TypeConstants.LARGER_OPERATION
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
        Pair(Triple(DECIMAL_TYPE, PLUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(INT_TYPE, PLUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(DECIMAL_TYPE, PLUS_OPERATION, INT_TYPE), DECIMAL_TYPE),

        //Subtraction
        Pair(Triple(INT_TYPE, MINUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DECIMAL_TYPE, MINUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(INT_TYPE, MINUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(DECIMAL_TYPE, MINUS_OPERATION, INT_TYPE), DECIMAL_TYPE),

        //Multiplication
        Pair(Triple(INT_TYPE, TIMES_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DECIMAL_TYPE, TIMES_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(INT_TYPE, TIMES_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(DECIMAL_TYPE, TIMES_OPERATION, INT_TYPE), DECIMAL_TYPE),

        //Division
        Pair(Triple(INT_TYPE, DIVIDE_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DECIMAL_TYPE, DIVIDE_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(INT_TYPE, DIVIDE_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(DECIMAL_TYPE, DIVIDE_OPERATION, INT_TYPE), DECIMAL_TYPE),

        //Exponentiation
        Pair(Triple(INT_TYPE, POWER_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(DECIMAL_TYPE, POWER_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(INT_TYPE, POWER_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(DECIMAL_TYPE, POWER_OPERATION, INT_TYPE), DECIMAL_TYPE),

        //Prefix plus/minus
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_MINUS_OPERATION, INT_TYPE), INT_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_MINUS_OPERATION, DECIMAL_TYPE), DECIMAL_TYPE),

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
        Pair(Triple(DECIMAL_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, DECIMAL_TYPE), STRING_TYPE),

        //Larger
        Pair(Triple(INT_TYPE, LARGER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, LARGER_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, LARGER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, LARGER_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),

        //Larger Equals
        Pair(Triple(INT_TYPE, LARGER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, LARGER_EQUALS_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, LARGER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, LARGER_EQUALS_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),

        //Smaller
        Pair(Triple(INT_TYPE, SMALLER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, SMALLER_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, SMALLER_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, SMALLER_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),

        //Smaller Equals
        Pair(Triple(INT_TYPE, SMALLER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT_TYPE, SMALLER_EQUALS_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, SMALLER_EQUALS_OPERATION, INT_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DECIMAL_TYPE, SMALLER_EQUALS_OPERATION, DECIMAL_TYPE), BOOLEAN_TYPE),

        //Repeat string
        Pair(Triple(STRING_TYPE, TIMES_OPERATION, INT_TYPE), STRING_TYPE),
        Pair(Triple(INT_TYPE, TIMES_OPERATION, STRING_TYPE), STRING_TYPE),
        )

    fun getSimpleTypes(triple : Triple<JSONStatement, JSONStatement, JSONStatement>) : JSONStatement?
    {
        val newTriple = Triple(makeTypeUngeneric(triple.first), triple.second, makeTypeUngeneric(triple.third))

        if((newTriple.second.name == EQUALS_ID || newTriple.second.name == NOT_EQUAL_ID)
                   && (isFormerSuperTypeOfLatter(NOTHING_TYPE, newTriple.first)
                    || isFormerSuperTypeOfLatter(NOTHING_TYPE, newTriple.third)
                    || isFormerSuperTypeOfLatter(newTriple.first,newTriple.third)
                    || isFormerSuperTypeOfLatter(newTriple.third,newTriple.first)))
            return BOOLEAN_TYPE

        if(newTriple.second == MUTABLE_OPERATION) {
            assert(newTriple.first == NOTHING_TYPE)
            if(isFormerSuperTypeOfLatter(COLLECTION_OF_NULLABLE_ANYTHING,newTriple.third))
                return makeMutable(newTriple.third)
        }

        if(newTriple.second == ASSIGN_OPERATION && isFormerSuperTypeOfLatter(newTriple.first,newTriple.third))
        {
            return NOTHING_TYPE
        }

        return simpleTypes[newTriple]
    }
}