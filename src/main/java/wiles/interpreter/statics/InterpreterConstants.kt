package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.ObjectsMap
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.IGNORE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.TypeConstants.MODULO_TYPE
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.NULLABLE_ANYTHING_TYPE
import wiles.shared.constants.TypeConstants.PANIC_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.WRITELINE_TYPE
import wiles.shared.constants.Utils.createFunctionType
import java.util.function.Function

object InterpreterConstants {
    val defaultVariableMap = VariableMap()
    val objectsMap = ObjectsMap()
    private var maxReference = Long.MIN_VALUE

    fun newReference() = maxReference++

    val NOTHING_REF = newReference()
    val FALSE_REF = newReference()
    val TRUE_REF = newReference()
    private val WRITE_REF = newReference()
    private val WRITELINE_REF = newReference()
    private val PANIC_REF = newReference()
    private val IGNORE_REF = newReference()
    private val MODULO_REF = newReference()
    private val SIZE_REF = newReference()
    private val AS_TEXT_REF = newReference()
    val ZERO_REF = newReference()
    val MAXINT64_REF = newReference()

    private val SIZE_TYPE = createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    private val AS_TEXT_TYPE = createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))

    init{
        defaultVariableMap[NOTHING_ID] = NOTHING_REF
        defaultVariableMap[FALSE_ID] = FALSE_REF
        defaultVariableMap[TRUE_ID] = TRUE_REF
        defaultVariableMap["!write"] = WRITE_REF
        defaultVariableMap["!writeline"] = WRITELINE_REF
        defaultVariableMap["!panic"] = PANIC_REF
        defaultVariableMap["!ignore"] = IGNORE_REF
        defaultVariableMap["!modulo"] = MODULO_REF
        defaultVariableMap["!TYPE LIST; (TYPE ANYTHING)!size"] = SIZE_REF
        defaultVariableMap["!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text"] = AS_TEXT_REF

        objectsMap[NOTHING_REF] = ObjectDetails(null, NOTHING_TYPE)
        objectsMap[FALSE_REF] = ObjectDetails(false, BOOLEAN_TYPE)
        objectsMap[TRUE_REF] = ObjectDetails(true, BOOLEAN_TYPE)
        objectsMap[WRITE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]!!]!!.value
            print(value)
            NOTHING_REF
        }, WRITELINE_TYPE)
        objectsMap[WRITELINE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]!!]!!.value
            println(value)
            NOTHING_REF
        }, WRITELINE_TYPE)
        objectsMap[PANIC_REF] =  ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]]?.value as String?
            value?:throw PanicException()
            throw PanicException(value)
        }, PANIC_TYPE)
        objectsMap[IGNORE_REF] = ObjectDetails(Function<VariableMap, Long>{NOTHING_REF}, IGNORE_TYPE)
        objectsMap[MODULO_REF] = ObjectDetails(Function<VariableMap, Long>{
            val x = objectsMap[it["!x"]!!]!!.value as Long
            val y = objectsMap[it["!y"]!!]!!.value as Long
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(x % y, INT64_TYPE)
            ref
        }, MODULO_TYPE)
        objectsMap[SIZE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!elem"]!!]!!.value as MutableList<*>
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(value.size, INT64_TYPE)
            ref
        }, SIZE_TYPE)
        objectsMap[AS_TEXT_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!elem"]!!]!!.value
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(
                when(value)
                {
                    null -> "nothing"
                    is Function<*, *> -> objectsMap[it["!elem"]!!]!!.type.components[0].toString()
                    else -> value.toString()
                }, STRING_TYPE)
            ref
        }, AS_TEXT_TYPE)
        objectsMap[ZERO_REF] = ObjectDetails(0L, INT64_TYPE)
        objectsMap[MAXINT64_REF] = ObjectDetails(Long.MAX_VALUE, INT64_TYPE)
    }

}