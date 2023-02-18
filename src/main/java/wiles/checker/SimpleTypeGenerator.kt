package wiles.checker

import wiles.checker.CheckerConstants.AND_OPERATION
import wiles.checker.CheckerConstants.APPLY_OPERATION
import wiles.checker.CheckerConstants.ASSIGN_OPERATION
import wiles.checker.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.CheckerConstants.DIVIDE_OPERATION
import wiles.checker.CheckerConstants.DOUBLE_TYPE
import wiles.checker.CheckerConstants.ELEM_ACCESS_OPERATION
import wiles.checker.CheckerConstants.ELEM_ACCESS_REF_OPERATION
import wiles.checker.CheckerConstants.EQUALS_OPERATION
import wiles.checker.CheckerConstants.INT64_TYPE
import wiles.checker.CheckerConstants.LARGER_EQUALS_OPERATION
import wiles.checker.CheckerConstants.LARGER_OPERATION
import wiles.checker.CheckerConstants.MINUS_OPERATION
import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.CheckerConstants.NOT_EQUAL_OPERATION
import wiles.checker.CheckerConstants.NOT_OPERATION
import wiles.checker.CheckerConstants.OR_OPERATION
import wiles.checker.CheckerConstants.PLUS_OPERATION
import wiles.checker.CheckerConstants.POWER_OPERATION
import wiles.checker.CheckerConstants.SMALLER_EQUALS_OPERATION
import wiles.checker.CheckerConstants.SMALLER_OPERATION
import wiles.checker.CheckerConstants.STRING_TYPE
import wiles.checker.CheckerConstants.TIMES_OPERATION
import wiles.checker.CheckerConstants.UNARY_MINUS_OPERATION
import wiles.checker.CheckerConstants.UNARY_PLUS_OPERATION
import wiles.checker.InferrerUtils.isFormerSuperTypeOfLatter
import wiles.checker.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.constants.Types.LIST_ID

object SimpleTypeGenerator {

    private val simpleTypes = mapOf(
        //Addition
        Pair(Triple(INT64_TYPE, PLUS_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT64_TYPE, PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, INT64_TYPE), DOUBLE_TYPE),

        //Subtraction
        Pair(Triple(INT64_TYPE, MINUS_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(DOUBLE_TYPE, MINUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT64_TYPE, MINUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, MINUS_OPERATION, INT64_TYPE), DOUBLE_TYPE),

        //Multiplication
        Pair(Triple(INT64_TYPE, TIMES_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(DOUBLE_TYPE, TIMES_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT64_TYPE, TIMES_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, TIMES_OPERATION, INT64_TYPE), DOUBLE_TYPE),

        //Division
        Pair(Triple(INT64_TYPE, DIVIDE_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(DOUBLE_TYPE, DIVIDE_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT64_TYPE, DIVIDE_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, DIVIDE_OPERATION, INT64_TYPE), DOUBLE_TYPE),

        //Exponentiation
        Pair(Triple(INT64_TYPE, POWER_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(DOUBLE_TYPE, POWER_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(INT64_TYPE, POWER_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(DOUBLE_TYPE, POWER_OPERATION, INT64_TYPE), DOUBLE_TYPE),

        //Prefix plus/minus
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, INT64_TYPE), INT64_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_PLUS_OPERATION, DOUBLE_TYPE), DOUBLE_TYPE),
        Pair(Triple(NOTHING_TYPE, UNARY_MINUS_OPERATION, INT64_TYPE), INT64_TYPE),
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
        Pair(Triple(INT64_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, INT64_TYPE), STRING_TYPE),

        //String and double concatenation
        Pair(Triple(DOUBLE_TYPE, PLUS_OPERATION, STRING_TYPE), STRING_TYPE),
        Pair(Triple(STRING_TYPE, PLUS_OPERATION, DOUBLE_TYPE), STRING_TYPE),

        //Equals
        Pair(Triple(INT64_TYPE, EQUALS_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(BOOLEAN_TYPE, EQUALS_OPERATION, BOOLEAN_TYPE), BOOLEAN_TYPE),
        Pair(Triple(STRING_TYPE, EQUALS_OPERATION, STRING_TYPE), BOOLEAN_TYPE),

        //Not equals
        Pair(Triple(INT64_TYPE, NOT_EQUAL_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(BOOLEAN_TYPE, NOT_EQUAL_OPERATION, BOOLEAN_TYPE), BOOLEAN_TYPE),
        Pair(Triple(STRING_TYPE, NOT_EQUAL_OPERATION, STRING_TYPE), BOOLEAN_TYPE),

        //Larger
        Pair(Triple(INT64_TYPE, LARGER_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT64_TYPE, LARGER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Larger Equals
        Pair(Triple(INT64_TYPE, LARGER_EQUALS_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT64_TYPE, LARGER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_EQUALS_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, LARGER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Smaller
        Pair(Triple(INT64_TYPE, SMALLER_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT64_TYPE, SMALLER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),

        //Smaller Equals
        Pair(Triple(INT64_TYPE, SMALLER_EQUALS_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(INT64_TYPE, SMALLER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_EQUALS_OPERATION, INT64_TYPE), BOOLEAN_TYPE),
        Pair(Triple(DOUBLE_TYPE, SMALLER_EQUALS_OPERATION, DOUBLE_TYPE), BOOLEAN_TYPE),
    )

    fun getSimpleTypes(triple : Triple<JSONStatement, JSONStatement, JSONStatement>) : JSONStatement?
    {
        if(triple.second in arrayOf(ELEM_ACCESS_OPERATION, ELEM_ACCESS_REF_OPERATION))
        {
            if(triple.first.name == LIST_ID && isFormerSuperTypeOfLatter(INT64_TYPE,triple.third))
            {
                assert(triple.first.components.size == 1)
                if(triple.second == ELEM_ACCESS_OPERATION)
                    return makeNullable(triple.first.components[0])
                return triple.first.components[0]
            }
        }

        if(triple.second == APPLY_OPERATION) {
            TODO()
        }

        if(triple.second == ASSIGN_OPERATION && isFormerSuperTypeOfLatter(triple.first,triple.third))
        {
            return NOTHING_TYPE
        }

        return simpleTypes[triple]
    }
}