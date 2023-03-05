package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.ObjectsMap
import wiles.interpreter.data.VariableDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.CheckerConstants.BOOLEAN_TYPE
import wiles.shared.constants.CheckerConstants.IGNORE_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.MODULO_TYPE
import wiles.shared.constants.CheckerConstants.NOTHING_TYPE
import wiles.shared.constants.CheckerConstants.NULLABLE_ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.CheckerConstants.WRITELINE_TYPE
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
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

    private val SIZE_TYPE = createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    private val AS_TEXT_TYPE = createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))

    init{
        defaultVariableMap[NOTHING_ID] = VariableDetails(NOTHING_REF, NOTHING_TYPE)
        defaultVariableMap[FALSE_ID] = VariableDetails(FALSE_REF, BOOLEAN_TYPE)
        defaultVariableMap[TRUE_ID] = VariableDetails(TRUE_REF, BOOLEAN_TYPE)
        defaultVariableMap["!write"] = VariableDetails(WRITE_REF, WRITELINE_TYPE)
        defaultVariableMap["!writeline"] = VariableDetails(WRITELINE_REF, WRITELINE_TYPE)
        defaultVariableMap["!panic"] = VariableDetails(PANIC_REF, WRITELINE_TYPE)
        defaultVariableMap["!ignore"] = VariableDetails(IGNORE_REF,IGNORE_TYPE)
        defaultVariableMap["!modulo"] = VariableDetails(MODULO_REF,MODULO_TYPE)
        defaultVariableMap["!TYPE LIST; (TYPE ANYTHING)!size"] = VariableDetails(SIZE_REF, SIZE_TYPE)
        defaultVariableMap["!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text"] =
            VariableDetails(AS_TEXT_REF, AS_TEXT_TYPE)

        objectsMap[NOTHING_REF] = ObjectDetails(null, NOTHING_TYPE)
        objectsMap[FALSE_REF] = ObjectDetails(false, BOOLEAN_TYPE)
        objectsMap[TRUE_REF] = ObjectDetails(true, BOOLEAN_TYPE)
        objectsMap[WRITE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]!!.reference]!!.value
            print(value)
            NOTHING_REF
        }, WRITELINE_TYPE)
        objectsMap[WRITELINE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]!!.reference]!!.value
            println(value)
            NOTHING_REF
        }, WRITELINE_TYPE)
        objectsMap[PANIC_REF] =  ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!text"]!!.reference]!!.value as String
            throw PanicException(value)
        }, WRITELINE_TYPE)
        objectsMap[IGNORE_REF] = ObjectDetails(Function<VariableMap, Long>{NOTHING_REF}, IGNORE_TYPE)
        objectsMap[MODULO_REF] = ObjectDetails(Function<VariableMap, Long>{
            val x = objectsMap[it["!x"]!!.reference]!!.value as Long
            val y = objectsMap[it["!y"]!!.reference]!!.value as Long
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(x % y, INT64_TYPE)
            ref
        }, MODULO_TYPE)
        objectsMap[SIZE_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!elem"]!!.reference]!!.value as MutableList<*>
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(value.size, INT64_TYPE)
            ref
        }, SIZE_TYPE)
        objectsMap[AS_TEXT_REF] = ObjectDetails(Function<VariableMap, Long>{
            val value = objectsMap[it["!elem"]!!.reference]!!.value
            val ref = newReference()
            objectsMap[ref] = ObjectDetails(""+(value?:"nothing"), STRING_TYPE)
            ref
        }, AS_TEXT_TYPE)
    }

}