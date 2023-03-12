package wiles.shared.constants

import wiles.checker.Checker
import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.VariableDetails
import wiles.checker.services.AccessOperationIdentifiers
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import java.util.function.Function


object StandardLibrary {
    fun getVariables(): CheckerVariableMap {
        val vars = CheckerVariableMap(
            hashMapOf(
                Pair(Tokens.TRUE_ID, VariableDetails(TypeConstants.BOOLEAN_TYPE)),
                Pair(Tokens.FALSE_ID, VariableDetails(TypeConstants.BOOLEAN_TYPE)),
                Pair(Tokens.NOTHING_ID, VariableDetails(TypeConstants.NOTHING_TYPE)),
                Pair("!write", VariableDetails(TypeConstants.WRITELINE_TYPE)),
                Pair("!writeline", VariableDetails(TypeConstants.WRITELINE_TYPE)),
                Pair("!panic", VariableDetails(TypeConstants.PANIC_TYPE)),
                Pair("!ignore", VariableDetails(TypeConstants.IGNORE_TYPE)),
                Pair("!modulo", VariableDetails(TypeConstants.MODULO_TYPE)),
                Pair("!read_int", VariableDetails(TypeConstants.READ_NOTHING_RETURN_INT_TYPE)),
                Pair("!read_line", VariableDetails(TypeConstants.READ_NOTHING_RETURN_STRING_TYPE)),
                Pair("!read_rational", VariableDetails(TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE)),
                Pair("!read_truth", VariableDetails(TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE)),
                Pair("!as_text", VariableDetails(TypeConstants.AS_TEXT_TYPE)),
                Pair("!as_list", VariableDetails(TypeConstants.AS_LIST_TYPE)),
                Pair("!Infinity", VariableDetails(TypeConstants.DOUBLE_TYPE)),
                Pair("!NaN", VariableDetails(TypeConstants.DOUBLE_TYPE)),
            )
        ).copy()
        vars.putAll(AccessOperationIdentifiers.getVariables())
        return vars
    }

    val defaultVariableMap = InterpreterVariableMap()

    private val SIZE_TYPE =
        Utils.createFunctionType(Pair(TypeConstants.LIST_OF_ANYTHING_TYPE, TypeConstants.INT64_TYPE))
    val NOTHING_REF = ObjectDetails(null, TypeConstants.NOTHING_TYPE)
    val FALSE_REF = ObjectDetails(false, TypeConstants.BOOLEAN_TYPE)
    val TRUE_REF = ObjectDetails(true, TypeConstants.BOOLEAN_TYPE)
    private val INFINITY_REF = ObjectDetails(Double.POSITIVE_INFINITY, TypeConstants.DOUBLE_TYPE)
    private val NAN_REF = ObjectDetails(Double.NaN, TypeConstants.DOUBLE_TYPE)
    private val WRITE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]!!
        print(value)
        NOTHING_REF
    }, TypeConstants.WRITELINE_TYPE)
    private val WRITELINE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]!!
        println(value)
        NOTHING_REF
    }, TypeConstants.WRITELINE_TYPE)
    private val PANIC_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]?.value as String?
        value?:throw PanicException()
        throw PanicException(value)
    }, TypeConstants.PANIC_TYPE)
    private val IGNORE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{NOTHING_REF}, TypeConstants.IGNORE_TYPE)
    private val MODULO_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val x = it["!x"]!!.value as Long
        val y =it["!y"]!!.value as Long
        ObjectDetails(x % y, TypeConstants.INT64_TYPE)
    }, TypeConstants.MODULO_TYPE)
    private val LIST_SIZE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as MutableList<*>
        ObjectDetails(value.size, TypeConstants.INT64_TYPE)
    }, SIZE_TYPE)
    private val TEXT_SIZE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as String
        ObjectDetails(value.length, TypeConstants.INT64_TYPE)
    }, SIZE_TYPE)
    private val AS_TEXT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        ObjectDetails(it["!elem"]!!.toString(), TypeConstants.STRING_TYPE)
    }, TypeConstants.AS_TEXT_TYPE)
    val ZERO_REF = ObjectDetails(0L, TypeConstants.INT64_TYPE)
    val MAX_INT64_REF = ObjectDetails(Long.MAX_VALUE, TypeConstants.INT64_TYPE)

    private val READ_INT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextLong())
            throw PanicException("Cannot read int value!")
        ObjectDetails(Checker.scanner.nextLong(), TypeConstants.INT64_TYPE)
    }, TypeConstants.READ_NOTHING_RETURN_INT_TYPE)
    private val READ_LINE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextLine())
            throw PanicException("Cannot read text value!")
        ObjectDetails(Checker.scanner.nextLine(), TypeConstants.STRING_TYPE)
    }, TypeConstants.READ_NOTHING_RETURN_STRING_TYPE)
    private val READ_RATIONAL_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextDouble())
            throw PanicException("Cannot read rational value!")
        ObjectDetails(Checker.scanner.nextDouble(), TypeConstants.DOUBLE_TYPE)
    }, TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE)
    private val READ_TRUTH_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextBoolean())
            throw PanicException("Cannot read truth value!")
        ObjectDetails(Checker.scanner.nextBoolean(), TypeConstants.BOOLEAN_TYPE)
    }, TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE)
    private val AS_LIST_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val elem = (map["!elem"]!!.value as String)
        ObjectDetails(elem.toMutableList().map {
            ObjectDetails(it.toString(), TypeConstants.STRING_TYPE) }, TypeConstants.LIST_OF_STRING)
    }, TypeConstants.AS_LIST_TYPE)

    init{
        defaultVariableMap[Tokens.NOTHING_ID] = NOTHING_REF
        defaultVariableMap[Tokens.FALSE_ID] = FALSE_REF
        defaultVariableMap[Tokens.TRUE_ID] = TRUE_REF
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
        defaultVariableMap["!Infinity"] = INFINITY_REF
        defaultVariableMap["!NaN"] = NAN_REF
    }
}