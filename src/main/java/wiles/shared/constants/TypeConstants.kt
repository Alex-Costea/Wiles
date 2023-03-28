package wiles.shared.constants

import wiles.checker.statics.InferrerUtils.makeGeneric
import wiles.checker.statics.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.STRING_START
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.TypeUtils.makeEither
import wiles.shared.constants.TypeUtils.makeList
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID
import wiles.shared.constants.Types.TYPE_TYPE_ID

object TypeConstants {

    val NOTHING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = NOTHING_ID)
    val BOOLEAN_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.BOOLEAN_ID)
    val INT64_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.INT64_ID)
    val STRING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = STRING_ID)
    val DOUBLE_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.DOUBLE_ID)
    val ERROR_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Tokens.ERROR_TOKEN)
    private val ANYTHING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = ANYTHING_ID)
    val METHOD_CALL_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = METHOD_CALL_ID)
    private val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    private val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val NOTHING_TOKEN = JSONStatement(syntaxType = SyntaxType.TOKEN, name = NOTHING_ID)

    val NULLABLE_STRING = makeNullable(STRING_TYPE)

    val PLUS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.PLUS_ID)
    val MINUS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.MINUS_ID)
    val TIMES_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.TIMES_ID)
    val DIVIDE_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.DIVIDE_ID)
    val POWER_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.POWER_ID)
    val UNARY_PLUS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.UNARY_PLUS_ID)
    val UNARY_MINUS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.UNARY_MINUS_ID)
    val AND_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.AND_ID)
    val OR_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.OR_ID)
    val NOT_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.NOT_ID)
    val EQUALS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.EQUALS_ID)
    val NOT_EQUAL_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.NOT_EQUAL_ID)
    val LARGER_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.LARGER_ID)
    val LARGER_EQUALS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.LARGER_EQUALS_ID)
    val SMALLER_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.SMALLER_ID)
    val SMALLER_EQUALS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.SMALLER_EQUALS_ID)
    val ACCESS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.ACCESS_ID)
    val ELEM_ACCESS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.ELEM_ACCESS_ID)
    val APPLY_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.APPLY_ID)
    val ASSIGN_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.ASSIGN_ID)
    val MUTABLE_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.MUTABLE_ID)

    val WRITE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, syntaxType = SyntaxType.TYPE),
                        JSONStatement(name = "!text", syntaxType = SyntaxType.TOKEN),
                    )
                ))
        ))
    )

    val WRITELINE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, syntaxType = SyntaxType.TYPE),
                        JSONStatement(name = "!text", syntaxType = SyntaxType.TOKEN),
                        JSONStatement(syntaxType = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = STRING_ID, syntaxType =  SyntaxType.TYPE),
                            JSONStatement(name = STRING_START, syntaxType =  SyntaxType.TOKEN),
                        ))
                    )
                ))
        ))
    )

    val PANIC_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = EITHER_ID, syntaxType = SyntaxType.TYPE,
                            components = mutableListOf(
                                JSONStatement(name = STRING_ID, syntaxType = SyntaxType.TYPE),
                                JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TYPE),
                            )),
                        JSONStatement(name = "!text", syntaxType = SyntaxType.TOKEN),
                        JSONStatement(syntaxType = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = NOTHING_ID, syntaxType =  SyntaxType.TYPE),
                            JSONStatement(name = NOTHING_ID, syntaxType =  SyntaxType.TOKEN),
                        )
                    )
                )
                )
            )
        ))
    )

    val IGNORE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        NULLABLE_ANYTHING_TYPE,
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )
                ))
        ))
    )

    val MODULO_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                INT64_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!x", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!y", syntaxType = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val READ_NOTHING_RETURN_INT_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(INT64_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_STRING_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(STRING_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_BOOL_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(BOOLEAN_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_DOUBLE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(DOUBLE_TYPE)
        ))
    )

    val LIST_OF_STRING = makeList(STRING_TYPE)

    private const val SET_VALUE_GENERIC_NAME = "!T|set"
    private val SET_VALUE_GENERIC_TYPE = makeGeneric(NULLABLE_ANYTHING_TYPE, SET_VALUE_GENERIC_NAME)
    val SET_VALUE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(NOTHING_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        makeMutable(SET_VALUE_GENERIC_TYPE),
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        SET_VALUE_GENERIC_TYPE,
                        JSONStatement(name = "!value", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                )
        ))
    )

    private const val MAYBE_GENERIC_NAME = "!T|maybe"
    private val MAYBE_GENERIC_TYPE = makeGeneric(ANYTHING_TYPE, MAYBE_GENERIC_NAME)
    val MAYBE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(makeNullable(MAYBE_GENERIC_TYPE),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        MAYBE_GENERIC_TYPE,
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )
                ),
            )
        ))
    )

    private const val CLONE_GENERIC_NAME = "!T|clone"
    private val CLONE_GENERIC_TYPE = makeGeneric(ANYTHING_TYPE, CLONE_GENERIC_NAME)
    val CLONE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(CLONE_GENERIC_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        CLONE_GENERIC_TYPE,
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )),
                    JSONStatement(syntaxType = SyntaxType.DECLARATION,
                        components = mutableListOf(
                            BOOLEAN_TYPE,
                            JSONStatement(name = "!deep", syntaxType = SyntaxType.TOKEN),
                            JSONStatement(syntaxType = SyntaxType.EXPRESSION, components =
                                mutableListOf(JSONStatement(name = TRUE_ID, syntaxType = SyntaxType.TOKEN)))
                        ),
                ),
            )
        ))
    )

    private const val GET_TYPE_GENERIC_NAME = "!T|type"
    private val GET_TYPE_GENERIC_TYPE = makeGeneric(NULLABLE_ANYTHING_TYPE, GET_TYPE_GENERIC_NAME)
    val GET_TYPE_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = TYPE_TYPE_ID, syntaxType = SyntaxType.TYPE,
                    components = mutableListOf(GET_TYPE_GENERIC_TYPE)),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        GET_TYPE_GENERIC_TYPE,
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )
                ),
            )
        ))
    )

    private val RUN_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), "!T|run")
    private val RUN_SUBTYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE)
        ))
    )
    val RUN_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        RUN_SUBTYPE,
                        JSONStatement(name = "!func", syntaxType = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    private val ADD_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), "!T|add")
    private val ADD_LIST_TYPE = makeMutable(makeList(makeNullable(ADD_GENERIC_TYPE)))
    val ADD_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(
                NOTHING_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        ADD_LIST_TYPE,
                        JSONStatement(name = "!list", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        ADD_GENERIC_TYPE,
                        JSONStatement(name = "!elem", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        makeNullable(INT64_TYPE),
                        JSONStatement(name = "!index", syntaxType = SyntaxType.TOKEN),
                        JSONStatement(name = NOTHING_ID, syntaxType =  SyntaxType.TOKEN),
                        )
                ),
            )
        ))
    )

    val AS_TEXT_TYPE = Utils.createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))
    val AS_LIST_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, LIST_OF_STRING))
    val SIZE_TYPE = Utils.createFunctionType(Pair(makeEither(mutableListOf(STRING_TYPE,LIST_OF_ANYTHING_TYPE)),
        INT64_TYPE))
}