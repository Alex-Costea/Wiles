package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
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

    private val SIZE_TYPE = createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    private val AS_TEXT_TYPE = createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))

    val NOTHING_REF = ObjectDetails(null, NOTHING_TYPE)
    val FALSE_REF = ObjectDetails(false, BOOLEAN_TYPE)
    val TRUE_REF = ObjectDetails(true, BOOLEAN_TYPE)
    private val WRITE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!text"]!!.value
        print(value)
        NOTHING_REF
    }, WRITELINE_TYPE)
    private val WRITELINE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!text"]!!.value
        println(value)
        NOTHING_REF
    }, WRITELINE_TYPE)
    private val PANIC_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!text"]?.value as String?
        value?:throw PanicException()
        throw PanicException(value)
    }, PANIC_TYPE)
    private val IGNORE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{NOTHING_REF}, IGNORE_TYPE)
    private val MODULO_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val x = it["!x"]!!.value as Long
        val y =it["!y"]!!.value as Long
        ObjectDetails(x % y, INT64_TYPE)
    }, MODULO_TYPE)
    private val SIZE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as MutableList<*>
        ObjectDetails(value.size, INT64_TYPE)
    }, SIZE_TYPE)
    private val AS_TEXT_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        ObjectDetails(it["!elem"]!!.toString(), STRING_TYPE)
    }, AS_TEXT_TYPE)
    val ZERO_REF = ObjectDetails(0L, INT64_TYPE)
    val MAXINT64_REF = ObjectDetails(Long.MAX_VALUE, INT64_TYPE)

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
    }

}