package wiles.interpreter

import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.CheckerConstants.ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.BOOLEAN_TYPE
import wiles.shared.constants.CheckerConstants.IGNORE_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.MODULO_TYPE
import wiles.shared.constants.CheckerConstants.NOTHING_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.CheckerConstants.WRITELINE_TYPE
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID

object InterpreterConstants {
    val defaultVariableMap = VariableMap()
    val objectsMap = ObjectsMap()

    private fun createFunctionType(variableType: Pair<JSONStatement, JSONStatement>): JSONStatement
    {
        return JSONStatement(type = SyntaxType.TYPE, name = Tokens.METHOD_ID,
            components = mutableListOf(
                JSONStatement(type = SyntaxType.METHOD,
                    components = mutableListOf(
                        variableType.second,
                        JSONStatement(type = SyntaxType.DECLARATION, name = Tokens.ANON_ARG_ID,
                            components = mutableListOf(variableType.first,
                                JSONStatement(type = SyntaxType.TOKEN, name = "!elem")
                            )),
                    ))
            ))
    }

    const val NOTHING_REF = -1L
    const val FALSE_REF = 0L
    const val TRUE_REF = 1L
    private const val WRITE_REF = 2L
    private const val WRITELINE_REF = 3L
    private const val PANIC_REF = 4L
    private const val IGNORE_REF = 5L
    private const val MODULO_REF = 6L
    private const val SIZE_REF = 7L
    private const val AS_TEXT_REF = 8L

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