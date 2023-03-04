package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.interpreter.data.ObjectsMap
import wiles.interpreter.data.VariableDetails
import wiles.interpreter.data.VariableMap
import wiles.shared.constants.CheckerConstants.ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.BOOLEAN_TYPE
import wiles.shared.constants.CheckerConstants.IGNORE_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.MODULO_TYPE
import wiles.shared.constants.CheckerConstants.NOTHING_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.CheckerConstants.WRITELINE_TYPE
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.Utils.createFunctionType

object InterpreterConstants {
    val defaultVariableMap = VariableMap()
    val objectsMap = ObjectsMap()

    private const val NOTHING_REF = Long.MIN_VALUE
    private const val FALSE_REF = NOTHING_REF + 1
    private const val TRUE_REF = FALSE_REF +1
    private const val WRITE_REF = TRUE_REF + 1
    private const val WRITELINE_REF = WRITE_REF + 1
    private const val PANIC_REF = WRITELINE_REF + 1
    private const val IGNORE_REF = PANIC_REF + 1
    private const val MODULO_REF = IGNORE_REF + 1
    private const val SIZE_REF = MODULO_REF + 1
    private const val AS_TEXT_REF = SIZE_REF + 1

    private val SIZE_TYPE = createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    private val AS_TEXT_TYPE = createFunctionType(Pair(ANYTHING_TYPE, STRING_TYPE))

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
        defaultVariableMap["!TYPE ANYTHING!as_text"] = VariableDetails(AS_TEXT_REF, AS_TEXT_TYPE)

        objectsMap[NOTHING_REF] = ObjectDetails(null, NOTHING_TYPE)
        objectsMap[FALSE_REF] = ObjectDetails(false, BOOLEAN_TYPE)
        objectsMap[TRUE_REF] = ObjectDetails(true, BOOLEAN_TYPE)
        objectsMap[WRITE_REF] = ObjectDetails({ it : String -> print(it)}, WRITELINE_TYPE)
        objectsMap[WRITELINE_REF] = ObjectDetails({ it : String -> println(it)}, WRITELINE_TYPE)
        objectsMap[PANIC_REF] = ObjectDetails({ it : String -> System.err.println(it)}, WRITELINE_TYPE)
        objectsMap[IGNORE_REF] = ObjectDetails({ _: Any -> }, IGNORE_TYPE)
        objectsMap[MODULO_REF] = ObjectDetails({ x : Long, y : Long -> x % y}, MODULO_TYPE)
        objectsMap[SIZE_REF] = ObjectDetails({ x : List<Any> -> x.size}, SIZE_TYPE)
        objectsMap[AS_TEXT_REF] = ObjectDetails({ x : Any? -> "" + (x?:"nothing")}, AS_TEXT_TYPE)
    }

    var maxReference = objectsMap.maxOf { it.key }
}