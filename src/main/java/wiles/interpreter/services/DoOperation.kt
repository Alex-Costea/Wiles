package wiles.interpreter.services

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.statics.InterpreterConstants.newReference
import wiles.interpreter.statics.InterpreterConstants.objectsMap
import wiles.shared.JSONStatement
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import java.util.function.ToLongBiFunction

object DoOperation {
    private fun createFunction(func : ToLongBiFunction<Any?,Any?>, type : JSONStatement) : ToLongBiFunction<Any?,Any?>
    {
        return ToLongBiFunction{ x: Any?, y: Any? ->
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(func.applyAsLong(x,y), type)
            return@ToLongBiFunction ref
        }
    }

    private val operationMap = hashMapOf(
        Pair("INT64|PLUS|INT64", createFunction({ x: Any?, y: Any? -> (x as Long) + (y as Long)}, INT64_TYPE)),
    )

    fun get(left : Long, middle : String, right : Long) : Long
    {
        val leftValue = objectsMap[left]!!.value
        val rightValue = objectsMap[right]!!.value
        return operationMap[middle]!!.applyAsLong(leftValue, rightValue)
    }
}