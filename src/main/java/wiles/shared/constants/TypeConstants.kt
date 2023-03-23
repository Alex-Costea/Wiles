package wiles.shared.constants

import wiles.checker.statics.InferrerUtils.makeGeneric
import wiles.checker.statics.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.STRING_START
import wiles.shared.constants.TypeUtils.makeList
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_VALUE_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID

object TypeConstants {

    val NOTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = NOTHING_ID)
    val BOOLEAN_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.BOOLEAN_ID)
    val INT64_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.INT64_ID)
    val STRING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = STRING_ID)
    val DOUBLE_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.DOUBLE_ID)
    val ERROR_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Tokens.ERROR_TOKEN)
    val GENERIC_VALUE_TYPE = JSONStatement(type = SyntaxType.TYPE, name = GENERIC_VALUE_ID)
    private val ANYTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = ANYTHING_ID)
    val METHOD_CALL_TYPE = JSONStatement(type = SyntaxType.TYPE, name = METHOD_CALL_ID)
    private val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    private val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val NOTHING_TOKEN = JSONStatement(type = SyntaxType.TOKEN, name = NOTHING_ID)

    val NULLABLE_STRING = makeNullable(STRING_TYPE)

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
    val MUTABLE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MUTABLE_ID)

    val WRITE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                    )
                ))
        ))
    )

    val WRITELINE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                        JSONStatement(type = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = STRING_ID, type =  SyntaxType.TYPE),
                            JSONStatement(name = STRING_START, type =  SyntaxType.TOKEN),
                        ))
                    )
                ))
        ))
    )

    val PANIC_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE,
                            components = mutableListOf(
                                JSONStatement(name = STRING_ID, type = SyntaxType.TYPE),
                                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                            )),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                        JSONStatement(type = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = NOTHING_ID, type =  SyntaxType.TYPE),
                            JSONStatement(name = NOTHING_ID, type =  SyntaxType.TOKEN),
                        )
                    )
                )
                )
            )
        ))
    )

    val IGNORE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        NULLABLE_ANYTHING_TYPE,
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ))
        ))
    )

    val MODULO_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                INT64_TYPE,
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!x", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!y", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val READ_NOTHING_RETURN_INT_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(INT64_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_STRING_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(STRING_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_BOOL_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(BOOLEAN_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_DOUBLE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(DOUBLE_TYPE)
        ))
    )

    val LIST_OF_STRING = makeList(STRING_TYPE)

    private const val SET_VALUE_GENERIC_NAME = "!T|set"
    private val SET_VALUE_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), SET_VALUE_GENERIC_NAME)
    val SET_VALUE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(NOTHING_TYPE,
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        makeMutable(SET_VALUE_GENERIC_TYPE),
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        SET_VALUE_GENERIC_TYPE,
                        JSONStatement(name = "!value", type = SyntaxType.TOKEN)
                    )
                ),
                )
        ))
    )

    private const val MAYBE_GENERIC_NAME = "!T|maybe"
    private val MAYBE_GENERIC_TYPE = makeGeneric(ANYTHING_TYPE, MAYBE_GENERIC_NAME)
    val MAYBE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(makeNullable(MAYBE_GENERIC_TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        MAYBE_GENERIC_TYPE,
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ),
            )
        ))
    )

    private val RUN_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), "!T|run")
    private val RUN_SUBTYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE)
        ))
    )
    val RUN_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE,
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        RUN_SUBTYPE,
                        JSONStatement(name = "!func", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val AS_TEXT_TYPE = Utils.createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))
    val AS_LIST_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, LIST_OF_STRING))
    val LIST_SIZE_TYPE = Utils.createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    val STRING_SIZE_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, INT64_TYPE))
}