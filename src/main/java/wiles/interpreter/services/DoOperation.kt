package wiles.interpreter.services

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.CheckerConstants.BOOLEAN_TYPE
import wiles.shared.constants.CheckerConstants.DOUBLE_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.Tokens.DIVIDE_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.LARGER_EQUALS_ID
import wiles.shared.constants.Tokens.LARGER_ID
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.Tokens.SMALLER_EQUALS_ID
import wiles.shared.constants.Tokens.SMALLER_ID
import wiles.shared.constants.Tokens.TIMES_ID
import wiles.shared.constants.Tokens.UNARY_MINUS_ID
import wiles.shared.constants.Tokens.UNARY_PLUS_ID
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.BOOLEAN_ID
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID
import java.util.function.BiFunction
import java.util.function.ToLongBiFunction
import kotlin.math.pow

object DoOperation {
    private fun createFunction(func : BiFunction<Any?, Any?, Any?>, type : JSONStatement) : ToLongBiFunction<Any?,Any?>
    {
        return ToLongBiFunction{ x: Any?, y: Any? ->
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(func.apply(x,y), type)
            return@ToLongBiFunction ref
        }
    }

    private val operationMap = hashMapOf(
        //Addition
        Pair("${INT64_ID}|${PLUS_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) + (y as Long)}, INT64_TYPE)),
        Pair("${INT64_ID}|${PLUS_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) + (y as Double)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${PLUS_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) + (y as Long)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${PLUS_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) + (y as Double)}, DOUBLE_TYPE)),

        //Subtraction
        Pair("${INT64_ID}|${MINUS_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) - (y as Long)}, INT64_TYPE)),
        Pair("${INT64_ID}|${MINUS_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) - (y as Double)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${MINUS_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) - (y as Long)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${MINUS_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) - (y as Double)}, DOUBLE_TYPE)),

        //Multiplication
        Pair("${INT64_ID}|${TIMES_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) * (y as Long)}, INT64_TYPE)),
        Pair("${INT64_ID}|${TIMES_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) * (y as Double)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${TIMES_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) * (y as Long)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${TIMES_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) * (y as Double)}, DOUBLE_TYPE)),

        //Division
        Pair("${INT64_ID}|${DIVIDE_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) / (y as Long)}, INT64_TYPE)),
        Pair("${INT64_ID}|${DIVIDE_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long) / (y as Double)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${DIVIDE_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) / (y as Long)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${DIVIDE_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double) / (y as Double)}, DOUBLE_TYPE)),

        //Exponentiation
        Pair("${INT64_ID}|${POWER_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long).toDouble().pow((y as Long).toDouble()).toLong() }, INT64_TYPE)),
        Pair("${INT64_ID}|${POWER_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Long).toDouble().pow(y as Double)}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${POWER_ID}|${INT64_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double).pow((y as Long).toDouble())}, DOUBLE_TYPE)),
        Pair("${DOUBLE_ID}|${POWER_ID}|${DOUBLE_ID}", createFunction({ x: Any?, y: Any? ->
            (x as Double).pow(y as Double)}, DOUBLE_TYPE)),

        //Prefix plus/minus
        Pair("${NOTHING_ID}|${UNARY_PLUS_ID}|${INT64_ID}",createFunction({ _: Any?, y: Any? ->
            (y as Long)}, INT64_TYPE)),
        Pair("${NOTHING_ID}|${UNARY_PLUS_ID}|${DOUBLE_ID}",createFunction({ _: Any?, y: Any? ->
            (y as Double)}, DOUBLE_TYPE)),
        Pair("${NOTHING_ID}|${UNARY_MINUS_ID}|${INT64_ID}",createFunction({ _: Any?, y: Any? ->
            -(y as Long)}, INT64_TYPE)),
        Pair("${NOTHING_ID}|${UNARY_MINUS_ID}|${DOUBLE_ID}",createFunction({ _: Any?, y: Any? ->
            -(y as Double)}, DOUBLE_TYPE)),

        //Not operation (other ones are handled seriously
        Pair("${NOTHING_ID}|${NOT_ID}|${BOOLEAN_ID}", createFunction({ _ : Any?, y : Any? ->
            !(y as Boolean)}, BOOLEAN_TYPE)),

        //String concatenation
        Pair("${STRING_ID}|${PLUS_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            (x as String) + (y as String)}, STRING_TYPE)),

        //String and boolean concatenation
        Pair("${STRING_ID}|${PLUS_ID}|${BOOLEAN_ID}", createFunction({ x : Any?, y : Any? ->
            (x as String) + (y as Boolean)}, STRING_TYPE)),
        Pair("${BOOLEAN_ID}|${PLUS_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Boolean).toString() + (y as String)}, STRING_TYPE)),

        //String and int concatenation
        Pair("${STRING_ID}|${PLUS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as String) + (y as Long)}, STRING_TYPE)),
        Pair("${INT64_ID}|${PLUS_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long).toString() + (y as String)}, STRING_TYPE)),

        //String and double concatenation
        Pair("${STRING_ID}|${PLUS_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as String) + (y as Double)}, STRING_TYPE)),
        Pair("${DOUBLE_ID}|${PLUS_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double).toString() + (y as String)}, STRING_TYPE)),

        //Equals
        Pair("${INT64_ID}|${EQUALS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            x == y}, INT64_TYPE)),
        Pair("${BOOLEAN_ID}|${EQUALS_ID}|${BOOLEAN_ID}", createFunction({ x : Any?, y : Any? ->
            x == y}, BOOLEAN_TYPE)),
        Pair("${STRING_ID}|${EQUALS_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            x == y}, STRING_TYPE)),

        //Not equals
        Pair("${INT64_ID}|${NOT_EQUAL_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            x != y}, INT64_TYPE)),
        Pair("${BOOLEAN_ID}|${NOT_EQUAL_ID}|${BOOLEAN_ID}", createFunction({ x : Any?, y : Any? ->
            x != y}, BOOLEAN_TYPE)),
        Pair("${STRING_ID}|${NOT_EQUAL_ID}|${STRING_ID}", createFunction({ x : Any?, y : Any? ->
            x != y}, STRING_TYPE)),

        //Larger
        Pair("${INT64_ID}|${LARGER_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) > (y as Long)}, BOOLEAN_TYPE)),
        Pair("${INT64_ID}|${LARGER_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) > (y as Double)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${LARGER_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) > (y as Long)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${LARGER_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) > (y as Double)}, BOOLEAN_TYPE)),

        //Larger equals
        Pair("${INT64_ID}|${LARGER_EQUALS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) >= (y as Long)}, BOOLEAN_TYPE)),
        Pair("${INT64_ID}|${LARGER_EQUALS_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) >= (y as Double)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${LARGER_EQUALS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) >= (y as Long)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${LARGER_EQUALS_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) >= (y as Double)}, BOOLEAN_TYPE)),

        //Smaller
        Pair("${INT64_ID}|${SMALLER_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) < (y as Long)}, BOOLEAN_TYPE)),
        Pair("${INT64_ID}|${SMALLER_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) < (y as Double)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${SMALLER_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) < (y as Long)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${SMALLER_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) < (y as Double)}, BOOLEAN_TYPE)),

        //Smaller equals
        Pair("${INT64_ID}|${SMALLER_EQUALS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) <= (y as Long)}, BOOLEAN_TYPE)),
        Pair("${INT64_ID}|${SMALLER_EQUALS_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Long) <= (y as Double)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${SMALLER_EQUALS_ID}|${INT64_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) <= (y as Long)}, BOOLEAN_TYPE)),
        Pair("${DOUBLE_ID}|${SMALLER_EQUALS_ID}|${DOUBLE_ID}", createFunction({ x : Any?, y : Any? ->
            (x as Double) <= (y as Double)}, BOOLEAN_TYPE)),
    )

    fun get(left : Long, middle : String, right : Long) : Long
    {
        val leftValue = objectsMap[left]!!.value
        val rightValue = objectsMap[right]!!.value

        return if(middle.contains(ANYTHING_ID)) {
            val operationNameSplit = middle.split("|").toMutableList()
            if (operationNameSplit[0] == ANYTHING_ID)
                operationNameSplit[0] = objectsMap[left]!!.type.name
            if (operationNameSplit[2] == ANYTHING_ID)
                operationNameSplit[2] = objectsMap[right]!!.type.name

            val operation = operationNameSplit[0] + "|" + operationNameSplit[1] + "|" + operationNameSplit[2]
            operationMap[operation]?.applyAsLong(leftValue, rightValue) ?: throw InternalErrorException()
        }
        else operationMap[middle]!!.applyAsLong(leftValue, rightValue)
    }
}