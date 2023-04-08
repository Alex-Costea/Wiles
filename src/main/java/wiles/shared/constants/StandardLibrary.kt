package wiles.shared.constants

import wiles.checker.Checker
import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.VariableDetails
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.statics.InterpreterConstants.toIntOrNull
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.CANNOT_READ_INT_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_RATIONAL_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_TEXT_ERROR
import wiles.shared.constants.ErrorMessages.CANNOT_READ_TRUTH_ERROR
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.TypeConstants.ADD_TYPE
import wiles.shared.constants.TypeConstants.AS_LIST_TYPE
import wiles.shared.constants.TypeConstants.AS_TEXT_TYPE
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeConstants.CLONE_TYPE
import wiles.shared.constants.TypeConstants.CONTENT_TYPE
import wiles.shared.constants.TypeConstants.DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.GET_TYPE_TYPE
import wiles.shared.constants.TypeConstants.IGNORE_TYPE
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_STRING
import wiles.shared.constants.TypeConstants.MAYBE_TYPE
import wiles.shared.constants.TypeConstants.MODULO_TYPE
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.PANIC_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_INT_TYPE
import wiles.shared.constants.TypeConstants.READ_NOTHING_RETURN_STRING_TYPE
import wiles.shared.constants.TypeConstants.REMOVE_AT_TYPE
import wiles.shared.constants.TypeConstants.RUN_TYPE
import wiles.shared.constants.TypeConstants.SET_AT_TYPE
import wiles.shared.constants.TypeConstants.SET_VALUE_TYPE
import wiles.shared.constants.TypeConstants.SIZE_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.WRITELINE_TYPE
import wiles.shared.constants.TypeConstants.WRITE_TYPE
import wiles.shared.constants.Types.TYPE_TYPE_ID
import java.util.function.Function

object StandardLibrary {
    val defaultInterpreterVars = InterpreterVariableMap()

    private const val WRITE = "!write"
    private const val WRITELINE = "!writeline"
    private const val PANIC = "!panic"
    private const val IGNORE = "!ignore"
    private const val MODULO = "!modulo"
    private const val SIZE = "!size"
    private const val READ_INT = "!read_int"
    private const val READ_LINE = "!read_line"
    private const val READ_RATIONAL = "!read_rational"
    private const val READ_TRUTH = "!read_truth"
    private const val AS_TEXT = "!as_text"
    private const val AS_LIST = "!as_list"
    private const val INFINITY = "!Infinity"
    private const val NAN = "!NaN"
    private const val SET_VALUE = "!set"
    private const val MAYBE = "!maybe"
    private const val CONTENT = "!content"
    private const val RUN = "!run"
    private const val GET_TYPE = "!type"
    private const val CLONE = "!clone"

    //CRUD
    private const val ADD = "!add"
    private const val SET_AT = "!update"
    private const val REMOVE_AT = "!remove"

    val defaultCheckerVars = CheckerVariableMap(
        hashMapOf(
            Pair(TRUE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(FALSE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(NOTHING_ID, VariableDetails(NOTHING_TYPE)),
            Pair(WRITE, VariableDetails(WRITE_TYPE)),
            Pair(WRITELINE, VariableDetails(WRITELINE_TYPE)),
            Pair(PANIC, VariableDetails(PANIC_TYPE)),
            Pair(IGNORE, VariableDetails(IGNORE_TYPE)),
            Pair(MODULO, VariableDetails(MODULO_TYPE)),
            Pair(SIZE, VariableDetails(SIZE_TYPE)),
            Pair(READ_INT, VariableDetails(READ_NOTHING_RETURN_INT_TYPE)),
            Pair(READ_LINE, VariableDetails(READ_NOTHING_RETURN_STRING_TYPE)),
            Pair(READ_RATIONAL, VariableDetails(READ_NOTHING_RETURN_DOUBLE_TYPE)),
            Pair(READ_TRUTH, VariableDetails(READ_NOTHING_RETURN_BOOL_TYPE)),
            Pair(AS_TEXT, VariableDetails(AS_TEXT_TYPE)),
            Pair(AS_LIST, VariableDetails(AS_LIST_TYPE)),
            Pair(INFINITY, VariableDetails(DOUBLE_TYPE)),
            Pair(NAN, VariableDetails(DOUBLE_TYPE)),
            Pair(SET_VALUE, VariableDetails(SET_VALUE_TYPE)),
            Pair(MAYBE, VariableDetails(MAYBE_TYPE)),
            Pair(CONTENT, VariableDetails(CONTENT_TYPE)),
            Pair(RUN, VariableDetails(RUN_TYPE)),
            Pair(ADD, VariableDetails(ADD_TYPE)),
            Pair(GET_TYPE, VariableDetails(GET_TYPE_TYPE)),
            Pair(CLONE, VariableDetails(CLONE_TYPE)),
            Pair(SET_AT, VariableDetails(SET_AT_TYPE)),
            Pair(REMOVE_AT, VariableDetails(REMOVE_AT_TYPE)),
        )
    )

    val NOTHING_REF = ObjectDetails(null, defaultCheckerVars[NOTHING_ID]!!.type)
    val FALSE_REF = ObjectDetails(false, defaultCheckerVars[FALSE_ID]!!.type)
    val TRUE_REF = ObjectDetails(true, defaultCheckerVars[TRUE_ID]!!.type)
    private val INFINITY_REF = ObjectDetails(Double.POSITIVE_INFINITY, defaultCheckerVars[INFINITY]!!.type)
    private val NAN_REF = ObjectDetails(Double.NaN, defaultCheckerVars[NAN]!!.type)

    private val WRITE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]?:return@Function NOTHING_REF
        print(value)
        NOTHING_REF
    }, defaultCheckerVars[WRITE]!!.type)

    private val WRITELINE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!text"]?:""
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

    private val SIZE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{
        val value = it["!elem"]!!.value
        if(value is Collection<*>)
            ObjectDetails(value.size.toLong(), INT64_TYPE)
        else ObjectDetails((value as String).length.toLong(), INT64_TYPE)
    }, defaultCheckerVars[SIZE]!!.type)

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

    private val SET_VALUE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val leftRef = map["!elem"]!!
        val mutableObj = map["!value"]!!.makeMutable()
        leftRef.setType(mutableObj.getType())
        leftRef.value = mutableObj.value
        NOTHING_REF
    }, defaultCheckerVars[SET_VALUE]!!.type)

    private val MAYBE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        map["!elem"]!!
    }, defaultCheckerVars[MAYBE]!!.type)

    private val CONTENT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val elem = map["!elem"]!!
        if(elem.value == null)
            throw PanicException("Content of element can't be retrieved because element is nothing.")
        elem
    }, defaultCheckerVars[CONTENT]!!.type)

    @Suppress("UNCHECKED_CAST")
    private val RUN_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val func = map["!func"]!!.value as Function<InterpreterVariableMap, ObjectDetails>
        func.apply(map)
    }, defaultCheckerVars[RUN]!!.type)

    @Suppress("UNCHECKED_CAST")
    private val ADD_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val list = map["!collection"]!!
        val elem = map["!value"]!!.makeMutable()
        val listValue = list.value as MutableList<ObjectDetails>
        val index = (map["!at"]!!.value as Long)
        try {
            listValue.add(index.toIntOrNull()!!, elem)
        }
        catch (ex : IndexOutOfBoundsException)
        {
            throw PanicException("Value out of bounds!")
        }
        catch (ex : NullPointerException)
        {
            throw PanicException("Value out of bounds!")
        }
        NOTHING_REF
    }, defaultCheckerVars[MAYBE]!!.type)

    @Suppress("UNCHECKED_CAST")
    private val SET_AT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val list = map["!collection"]!!
        val elem = map["!value"]!!.makeMutable()
        val listValue = list.value as MutableList<ObjectDetails>
        val index = (map["!at"]!!.value as Long)
        try {
            listValue[index.toIntOrNull()!!] = elem
        }
        catch (ex : IndexOutOfBoundsException)
        {
            throw PanicException("Value out of bounds!")
        }
        catch (ex : NullPointerException)
        {
            throw PanicException("Value out of bounds!")
        }
        NOTHING_REF
    }, defaultCheckerVars[MAYBE]!!.type)

    @Suppress("UNCHECKED_CAST")
    private val REMOVE_AT_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val list = map["!collection"]!!
        val listValue = list.value as MutableList<ObjectDetails>
        val index = (map["!at"]!!.value as Long)
        try {
            listValue.removeAt(index.toIntOrNull()!!)
        }
        catch (ex : IndexOutOfBoundsException)
        {
            throw PanicException("Value out of bounds!")
        }
        catch (ex : NullPointerException)
        {
            throw PanicException("Value out of bounds!")
        }
        NOTHING_REF
    }, defaultCheckerVars[MAYBE]!!.type)

    private val GET_TYPE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        val newType = map["!elem"]!!.getType().copy()
        ObjectDetails(newType, JSONStatement(name = TYPE_TYPE_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(newType)))
    }, defaultCheckerVars[MAYBE]!!.type)

    private val CLONE_REF = ObjectDetails(Function<InterpreterVariableMap, ObjectDetails>{ map ->
        map["!elem"]!!.clone(deep = (map["!deep"]?.value ?: true) as Boolean)
    }, defaultCheckerVars[MAYBE]!!.type)

    init{
        defaultInterpreterVars[NOTHING_ID] = NOTHING_REF
        defaultInterpreterVars[FALSE_ID] = FALSE_REF
        defaultInterpreterVars[TRUE_ID] = TRUE_REF
        defaultInterpreterVars[WRITE] = WRITE_REF
        defaultInterpreterVars[WRITELINE] = WRITELINE_REF
        defaultInterpreterVars[PANIC] = PANIC_REF
        defaultInterpreterVars[IGNORE] = IGNORE_REF
        defaultInterpreterVars[MODULO] = MODULO_REF
        defaultInterpreterVars[SIZE] = SIZE_REF
        defaultInterpreterVars[READ_INT] = READ_INT_REF
        defaultInterpreterVars[READ_LINE] = READ_LINE_REF
        defaultInterpreterVars[READ_RATIONAL] = READ_RATIONAL_REF
        defaultInterpreterVars[READ_TRUTH] = READ_TRUTH_REF
        defaultInterpreterVars[AS_TEXT] = AS_TEXT_REF
        defaultInterpreterVars[AS_LIST] = AS_LIST_REF
        defaultInterpreterVars[INFINITY] = INFINITY_REF
        defaultInterpreterVars[NAN] = NAN_REF
        defaultInterpreterVars[SET_VALUE] = SET_VALUE_REF
        defaultInterpreterVars[MAYBE] = MAYBE_REF
        defaultInterpreterVars[CONTENT] = CONTENT_REF
        defaultInterpreterVars[RUN] = RUN_REF
        defaultInterpreterVars[ADD] = ADD_REF
        defaultInterpreterVars[GET_TYPE] = GET_TYPE_REF
        defaultInterpreterVars[CLONE] = CLONE_REF
        defaultInterpreterVars[SET_AT] = SET_AT_REF
        defaultInterpreterVars[REMOVE_AT] = REMOVE_AT_REF
    }
}