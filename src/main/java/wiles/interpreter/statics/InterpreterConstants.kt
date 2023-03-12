package wiles.interpreter.statics

import wiles.checker.Checker.Companion.scanner
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.TypeConstants.AS_LIST_TYPE
import wiles.shared.constants.TypeConstants.AS_TEXT_TYPE
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.IGNORE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_STRING
import wiles.shared.constants.TypeConstants.MODULO_TYPE
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.PANIC_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_INT_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_STRING_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.WRITELINE_TYPE
import wiles.shared.constants.Utils.createFunctionType
import java.util.function.Function

object InterpreterConstants {
    val defaultVariableMap = VariableMap()

    private val SIZE_TYPE = createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))

    val NOTHING_REF = ObjectDetails(null, NOTHING_TYPE)
    val FALSE_REF = ObjectDetails(false, BOOLEAN_TYPE)
    val TRUE_REF = ObjectDetails(true, BOOLEAN_TYPE)
    private val WRITE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!text"]!!
        print(value)
        NOTHING_REF
    }, WRITELINE_TYPE)
    private val WRITELINE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!text"]!!
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
    private val LIST_SIZE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as MutableList<*>
        ObjectDetails(value.size, INT64_TYPE)
    }, SIZE_TYPE)
    private val TEXT_SIZE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as String
        ObjectDetails(value.length, INT64_TYPE)
    }, SIZE_TYPE)
    private val AS_TEXT_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        ObjectDetails(it["!elem"]!!.toString(), STRING_TYPE)
    }, AS_TEXT_TYPE)
    val ZERO_REF = ObjectDetails(0L, INT64_TYPE)
    val MAX_INT64_REF = ObjectDetails(Long.MAX_VALUE, INT64_TYPE)

    private val READ_INT_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        if(!scanner.hasNextLong())
            throw PanicException("Cannot read int value!")
        ObjectDetails(scanner.nextLong(), INT64_TYPE)
    }, READ_NOTHING_RETURN_INT_TYPE)
    private val READ_LINE_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        if(!scanner.hasNextLine())
            throw PanicException("Cannot read text value!")
        ObjectDetails(scanner.nextLine(), STRING_TYPE)
    }, READ_NOTHING_RETURN_STRING_TYPE)
    private val READ_RATIONAL_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        if(!scanner.hasNextDouble())
            throw PanicException("Cannot read rational value!")
        ObjectDetails(scanner.nextDouble(), DOUBLE_TYPE)
    }, READ_NOTHING_RETURN_DOUBLE_TYPE)
    private val READ_TRUTH_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{
        if(!scanner.hasNextBoolean())
            throw PanicException("Cannot read truth value!")
        ObjectDetails(scanner.nextBoolean(), BOOLEAN_TYPE)
    }, READ_NOTHING_RETURN_BOOL_TYPE)
    private val AS_LIST_REF = ObjectDetails(Function<VariableMap, ObjectDetails>{ map ->
        val elem = (map["!elem"]!!.value as String)
        ObjectDetails(elem.toMutableList().map {
            ObjectDetails(it.toString(), STRING_TYPE) }, LIST_OF_STRING)
    }, AS_LIST_TYPE)

    init{
        defaultVariableMap[NOTHING_ID] = NOTHING_REF
        defaultVariableMap[FALSE_ID] = FALSE_REF
        defaultVariableMap[TRUE_ID] = TRUE_REF
        defaultVariableMap["!write"] = WRITE_REF
        defaultVariableMap["!writeline"] = WRITELINE_REF
        defaultVariableMap["!panic"] = PANIC_REF
        defaultVariableMap["!ignore"] = IGNORE_REF
        defaultVariableMap["!modulo"] = MODULO_REF
        defaultVariableMap["!TYPE LIST; (TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING))!size"] = LIST_SIZE_REF
        defaultVariableMap["!TYPE STRING!size"] = TEXT_SIZE_REF
        defaultVariableMap["!as_text"] = AS_TEXT_REF
        defaultVariableMap["!read_int"] = READ_INT_REF
        defaultVariableMap["!read_truth"] = READ_TRUTH_REF
        defaultVariableMap["!read_rational"] = READ_RATIONAL_REF
        defaultVariableMap["!read_line"] = READ_LINE_REF
        defaultVariableMap["!as_list"] = AS_LIST_REF
    }

    fun Long.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

}