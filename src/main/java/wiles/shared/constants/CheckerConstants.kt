package wiles.shared.constants

import wiles.checker.statics.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.METHOD_CALL_ID

object CheckerConstants {

    fun makeMutable(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = Tokens.MUTABLE_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeList(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = Types.LIST_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeMethod(type : JSONStatement) : JSONStatement
    {
        val newType = type.copyRemovingLocation()
        newType.components.removeLast()
        return JSONStatement(name = Tokens.METHOD_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(newType))
    }

    val NOTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Tokens.NOTHING_ID)
    val BOOLEAN_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.BOOLEAN_ID)
    val INT64_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.INT64_ID)
    val STRING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.STRING_ID)
    val DOUBLE_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.DOUBLE_ID)
    val ERROR_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Tokens.ERROR_TOKEN)
    val ANYTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = ANYTHING_ID)
    val METHOD_CALL_TYPE = JSONStatement(type = SyntaxType.TYPE, name = METHOD_CALL_ID)
    val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val MUTABLE_ANYTHING_INCLUDING_NOTHING_TYPE = makeMutable(makeNullable(ANYTHING_TYPE))

    val NOTHING_TOKEN = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.NOTHING_ID)

    val PLUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.PLUS_ID)
    val MINUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MINUS_ID)
    val TIMES_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.TIMES_ID)
    val DIVIDE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.DIVIDE_ID)
    val POWER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.POWER_ID)
    val UNARY_PLUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.UNARY_PLUS_ID)
    val UNARY_MINUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.UNARY_MINUS_ID)
    val AND_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.AND_ID)
    val OR_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.OR_ID)
    val NOT_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.NOT_ID)
    val EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.EQUALS_ID)
    val NOT_EQUAL_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.NOT_EQUAL_ID)
    val LARGER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.LARGER_ID)
    val LARGER_EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.LARGER_EQUALS_ID)
    val SMALLER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.SMALLER_ID)
    val SMALLER_EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.SMALLER_EQUALS_ID)
    val ACCESS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ACCESS_ID)
    val ELEM_ACCESS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ELEM_ACCESS_ID)
    val APPLY_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.APPLY_ID)
    val ASSIGN_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ASSIGN_ID)
    val MODIFY_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MODIFY_ID)
    val MUTABLE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MUTABLE_ID)
    val IMPORT_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.IMPORT_ID)

    val WRITELINE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = Tokens.NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = Types.STRING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN)
                    )
                ))
        ))
    )


    val IGNORE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = Tokens.NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        NULLABLE_ANYTHING_TYPE,
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ))
        ))
    )

    val MODULO_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.METHOD,
            components = mutableListOf(
                INT64_TYPE,
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!x", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!y", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )
}