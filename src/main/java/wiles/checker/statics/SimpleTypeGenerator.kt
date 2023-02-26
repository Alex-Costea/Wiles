package wiles.checker.statics

import wiles.checker.statics.CheckerConstants.AND_OPERATION
import wiles.checker.statics.CheckerConstants.ASSIGN_OPERATION
import wiles.checker.statics.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.statics.CheckerConstants.DIVIDE_OPERATION
import wiles.checker.statics.CheckerConstants.DOUBLE_TYPE
import wiles.checker.statics.CheckerConstants.EQUALS_OPERATION
import wiles.checker.statics.CheckerConstants.IMPORT_OPERATION
import wiles.checker.statics.CheckerConstants.INT64_TYPE
import wiles.checker.statics.CheckerConstants.LARGER_EQUALS_OPERATION
import wiles.checker.statics.CheckerConstants.LARGER_OPERATION
import wiles.checker.statics.CheckerConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
import wiles.checker.statics.CheckerConstants.MINUS_OPERATION
import wiles.checker.statics.CheckerConstants.MODIFY_OPERATION
import wiles.checker.statics.CheckerConstants.MUTABLE_ANYTHING_INCLUDING_NOTHING_TYPE
import wiles.checker.statics.CheckerConstants.MUTABLE_OPERATION
import wiles.checker.statics.CheckerConstants.NOTHING_TYPE
import wiles.checker.statics.CheckerConstants.NOT_EQUAL_OPERATION
import wiles.checker.statics.CheckerConstants.NOT_OPERATION
import wiles.checker.statics.CheckerConstants.OR_OPERATION
import wiles.checker.statics.CheckerConstants.PLUS_OPERATION
import wiles.checker.statics.CheckerConstants.POWER_OPERATION
import wiles.checker.statics.CheckerConstants.SMALLER_EQUALS_OPERATION
import wiles.checker.statics.CheckerConstants.SMALLER_OPERATION
import wiles.checker.statics.CheckerConstants.STRING_TYPE
import wiles.checker.statics.CheckerConstants.TIMES_OPERATION
import wiles.checker.statics.CheckerConstants.UNARY_MINUS_OPERATION
import wiles.checker.statics.CheckerConstants.UNARY_PLUS_OPERATION
import wiles.checker.statics.InferrerUtils.isFormerSuperTypeOfLatter
import wiles.checker.statics.InferrerUtils.makeMutable
import wiles.checker.statics.InferrerUtils.unbox
import wiles.shared.JSONStatement

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
        val unboxedTriple = Triple(unbox(triple.first), triple.second, unbox(triple.third))

        if(unboxedTriple.second == IMPORT_OPERATION)
            return unboxedTriple.third.copyRemovingLocation()

        if(unboxedTriple.second == CheckerConstants.ELEM_ACCESS_OPERATION)
        {
            if(isFormerSuperTypeOfLatter(LIST_OF_NULLABLE_ANYTHING_TYPE,unboxedTriple.first)
                && isFormerSuperTypeOfLatter(INT64_TYPE,unboxedTriple.third))
            {
                return InferrerUtils.makeNullable(unboxedTriple.first.components[0])
            }
        }

        if(unboxedTriple.second == MUTABLE_OPERATION)
        {
            assert(unboxedTriple.first == NOTHING_TYPE)
            val result = makeMutable(unboxedTriple.third)
            if(isFormerSuperTypeOfLatter(LIST_OF_NULLABLE_ANYTHING_TYPE,unboxedTriple.third))
                result.components[0].components[0] = makeMutable(result.components[0].components[0])
            return result
        }

        if(unboxedTriple.second == ASSIGN_OPERATION && isFormerSuperTypeOfLatter(unboxedTriple.first,unboxedTriple.third))
        {
            return NOTHING_TYPE
        }

        if(unboxedTriple.second == MODIFY_OPERATION
            && isFormerSuperTypeOfLatter(MUTABLE_ANYTHING_INCLUDING_NOTHING_TYPE,triple.first)
            && isFormerSuperTypeOfLatter(unboxedTriple.first,unboxedTriple.third))
        {
            return NOTHING_TYPE
        }

        return simpleTypes[unboxedTriple]
    }
}