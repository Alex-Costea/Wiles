package wiles.shared.constants

import wiles.checker.Checker
import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.VariableDetails
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import wiles.shared.constants.ErrorMessages.CANNOT_READ_INT_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_RATIONAL_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_TEXT_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_TRUTH_ERROR
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.TypeConstants.AS_LIST_TYPE
import wiles.shared.constants.TypeConstants.AS_TEXT_TYPE
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.IGNORE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_STRING
import wiles.shared.constants.TypeConstants.LIST_SIZE_TYPE
import wiles.shared.constants.TypeConstants.MODULO_TYPE
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.PANIC_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_INT_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_STRING_TYPE
import wiles.shared.constants.TypeConstants.STRING_SIZE_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.WRITELINE_TYPE
import java.util.function.Function

object StandardLibrary {
    val defaultInterpreterVars = InterpreterVariableMap()

    private const val WRITE = "!write"
    private const val WRITELINE = "!writeline"
    private const val PANIC = "!panic"
    private const val IGNORE = "!ignore"
    private const val MODULO = "!modulo"
    private const val LIST_SIZE = "!TYPE LIST; (TYPE EITHER; (TYPE ANYTHING; TYPE !nothing))!size"
    private const val TEXT_SIZE = "!TYPE STRING!size"
    private const val READ_INT = "!read_int"
    private const val READ_LINE = "!read_line"
    private const val READ_RATIONAL = "!read_rational"
    private const val READ_TRUTH = "!read_truth"
    private const val AS_TEXT = "!as_text"
    private const val AS_LIST = "!as_list"
    private const val INFINITY = "!Infinity"
    private const val NAN = "!NaN"

    val defaultCheckerVars = CheckerVariableMap(
        hashMapOf(
            Pair(TRUE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(FALSE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(NOTHING_ID, VariableDetails(NOTHING_TYPE)),
            Pair(WRITE, VariableDetails(WRITELINE_TYPE)),
            Pair(WRITELINE, VariableDetails(WRITELINE_TYPE)),
            Pair(PANIC, VariableDetails(PANIC_TYPE)),
            Pair(IGNORE, VariableDetails(IGNORE_TYPE)),
            Pair(MODULO, VariableDetails(MODULO_TYPE)),
            Pair(LIST_SIZE, VariableDetails(LIST_SIZE_TYPE)),
            Pair(TEXT_SIZE, VariableDetails(STRING_SIZE_TYPE)),
            Pair(READ_INT, VariableDetails(READ_NOTHING_RETURN_INT_TYPE)),
            Pair(READ_LINE, VariableDetails(READ_NOTHING_RETURN_STRING_TYPE)),
            Pair(READ_RATIONAL, VariableDetails(READ_NOTHING_RETURN_DOUBLE_TYPE)),
            Pair(READ_TRUTH, VariableDetails(READ_NOTHING_RETURN_BOOL_TYPE)),
            Pair(AS_TEXT, VariableDetails(AS_TEXT_TYPE)),
            Pair(AS_LIST, VariableDetails(AS_LIST_TYPE)),
            Pair(INFINITY, VariableDetails(DOUBLE_TYPE)),
            Pair(NAN, VariableDetails(DOUBLE_TYPE)),
        )
    )

    val NOTHING_REF = ObjectDetails(null, defaultCheckerVars[NOTHING_ID]!!.type)
    val FALSE_REF = ObjectDetails(false, defaultCheckerVars[FALSE_ID]!!.type)
    val TRUE_REF = ObjectDetails(true, defaultCheckerVars[TRUE_ID]!!.type)
    private val INFINITY_REF = ObjectDetails(Double.POSITIVE_INFINITY, defaultCheckerVars[INFINITY]!!.type)
    private val NAN_REF = ObjectDetails(Double.NaN, defaultCheckerVars[NAN]!!.type)

    private val WRITE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]!!
        print(value)
        NOTHING_REF
    }, defaultCheckerVars[WRITE]!!.type)

    private val WRITELINE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]!!
        println(value)
        NOTHING_REF
    }, defaultCheckerVars[WRITELINE]!!.type)

    private val PANIC_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]?.value as String?
        value?:throw PanicException()
        throw PanicException(value)
    }, defaultCheckerVars[PANIC]!!.type)

    private val IGNORE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        NOTHING_REF}, defaultCheckerVars[IGNORE]!!.type)

    private val MODULO_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val x = it["!x"]!!.value as Long
        val y =it["!y"]!!.value as Long
        ObjectDetails(x % y, INT64_TYPE)
    }, defaultCheckerVars[MODULO]!!.type)

    private val LIST_SIZE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as MutableList<*>
        ObjectDetails(value.size.toLong(), INT64_TYPE)
    }, defaultCheckerVars[LIST_SIZE]!!.type)

    private val TEXT_SIZE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value as String
        ObjectDetails(value.length.toLong(), INT64_TYPE)
    }, defaultCheckerVars[TEXT_SIZE]!!.type)

    private val AS_TEXT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        ObjectDetails(it["!elem"]!!.toString(), STRING_TYPE)
    }, defaultCheckerVars[AS_TEXT]!!.type)

    private val READ_INT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextLong())
            throw PanicException(CANNOT_READ_INT_ERROR)
        ObjectDetails(Checker.scanner.nextLong(), INT64_TYPE)
    }, defaultCheckerVars[READ_INT]!!.type)

    private val READ_LINE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextLine())
            throw PanicException(CANNOT_READ_TEXT_ERROR)
        ObjectDetails(Checker.scanner.nextLine(), STRING_TYPE)
    }, defaultCheckerVars[READ_LINE]!!.type)

    private val READ_RATIONAL_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextDouble())
            throw PanicException(CANNOT_READ_RATIONAL_ERROR)
        ObjectDetails(Checker.scanner.nextDouble(), DOUBLE_TYPE)
    }, defaultCheckerVars[READ_RATIONAL]!!.type)

    private val READ_TRUTH_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        if(!Checker.scanner.hasNextBoolean())
            throw PanicException(CANNOT_READ_TRUTH_ERROR)
        ObjectDetails(Checker.scanner.nextBoolean(), BOOLEAN_TYPE)
    }, defaultCheckerVars[READ_TRUTH]!!.type)

    private val AS_LIST_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val elem = (map["!elem"]!!.value as String)
        ObjectDetails(elem.toMutableList().map {
            ObjectDetails(it.toString(), STRING_TYPE) }, LIST_OF_STRING)
    }, defaultCheckerVars[READ_LINE]!!.type)

    init{
        defaultInterpreterVars[NOTHING_ID] = NOTHING_REF
        defaultInterpreterVars[FALSE_ID] = FALSE_REF
        defaultInterpreterVars[TRUE_ID] = TRUE_REF
        defaultInterpreterVars[WRITE] = WRITE_REF
        defaultInterpreterVars[WRITELINE] = WRITELINE_REF
        defaultInterpreterVars[PANIC] = PANIC_REF
        defaultInterpreterVars[IGNORE] = IGNORE_REF
        defaultInterpreterVars[MODULO] = MODULO_REF
        defaultInterpreterVars[LIST_SIZE] = LIST_SIZE_REF
        defaultInterpreterVars[TEXT_SIZE] = TEXT_SIZE_REF
        defaultInterpreterVars[READ_INT] = READ_INT_REF
        defaultInterpreterVars[READ_LINE] = READ_LINE_REF
        defaultInterpreterVars[READ_RATIONAL] = READ_RATIONAL_REF
        defaultInterpreterVars[READ_TRUTH] = READ_TRUTH_REF
        defaultInterpreterVars[AS_TEXT] = AS_TEXT_REF
        defaultInterpreterVars[AS_LIST] = AS_LIST_REF
        defaultInterpreterVars[INFINITY] = INFINITY_REF
        defaultInterpreterVars[NAN] = NAN_REF
    }
}