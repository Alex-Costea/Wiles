package wiles.interpreter.statics

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

    init{
        defaultVariableMap[NOTHING_ID] = VariableDetails(NOTHING_REF, NOTHING_TYPE)
        defaultVariableMap[FALSE_ID] = VariableDetails(FALSE_REF, BOOLEAN_TYPE)
        defaultVariableMap[TRUE_ID] = VariableDetails(TRUE_REF, BOOLEAN_TYPE)
        defaultVariableMap["!write"] = VariableDetails(WRITE_REF, WRITELINE_TYPE)
        defaultVariableMap["!writeline"] = VariableDetails(WRITELINE_REF, WRITELINE_TYPE)
        defaultVariableMap["!panic"] = VariableDetails(PANIC_REF, WRITELINE_TYPE)
        defaultVariableMap["!ignore"] = VariableDetails(IGNORE_REF,IGNORE_TYPE)
        defaultVariableMap["!modulo"] = VariableDetails(MODULO_REF,MODULO_TYPE)
        defaultVariableMap["!TYPE LIST; (TYPE ANYTHING)!size"] = VariableDetails(SIZE_REF,
            createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE)))
        defaultVariableMap["!TYPE ANYTHING!as_text"] = VariableDetails(AS_TEXT_REF,
            createFunctionType(Pair(ANYTHING_TYPE, STRING_TYPE)))

        objectsMap[NOTHING_REF] = null
        objectsMap[FALSE_REF] = false
        objectsMap[TRUE_REF] = true
        objectsMap[WRITE_REF] = { it : String -> print(it)}
        objectsMap[WRITELINE_REF] = { it : String -> println(it)}
        objectsMap[PANIC_REF] = { it : String -> System.err.println(it)}
        objectsMap[IGNORE_REF] = { _: Any -> }
        objectsMap[MODULO_REF] = { x : Long, y : Long -> x % y}
        objectsMap[SIZE_REF] = { x : List<Any> -> x.size}
        objectsMap[AS_TEXT_REF] = { x : Any? -> "" + (x?:"nothing")}
    }

    var maxReference = objectsMap.maxOf { it.key }
}