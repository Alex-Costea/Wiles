package wiles.shared.constants

import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.DATA_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.STRING_START
import wiles.shared.constants.TypeUtils.makeCollection
import wiles.shared.constants.TypeUtils.makeEither
import wiles.shared.constants.TypeUtils.makeList
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.TypeUtils.makeNullable
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID
import wiles.shared.constants.Types.UNIVERSAL_SUBTYPE_ID

object TypeConstants {

    val NOTHING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = NOTHING_ID)
    val BOOLEAN_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.BOOLEAN_ID)
    val INT_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.INT_ID)
    val STRING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = STRING_ID)
    val DECIMAL_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Types.DECIMAL_ID)
    val ERROR_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = Tokens.ERROR_TOKEN)
    private val ANYTHING_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = ANYTHING_ID)
    val METHOD_CALL_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = METHOD_CALL_ID)
    @Suppress("UNUSED")
    val UNIVERSAL_SUBTYPE_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = UNIVERSAL_SUBTYPE_ID)
    val DATA_TYPE = JSONStatement(syntaxType = SyntaxType.TYPE, name = DATA_ID)
    private val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    private val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val NOTHING_TOKEN = JSONStatement(syntaxType = SyntaxType.TOKEN, name = NOTHING_ID)

    val MUTABLE_NULLABLE_ANYTHING = makeMutable(NULLABLE_ANYTHING_TYPE)

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
    val LARGER_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.LARGER_ID)
    val LARGER_EQUALS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.LARGER_EQUALS_ID)
    val SMALLER_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.SMALLER_ID)
    val SMALLER_EQUALS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.SMALLER_EQUALS_ID)
    val ACCESS_OPERATION = JSONStatement(syntaxType = SyntaxType.TOKEN, name = Tokens.ACCESS_ID)
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
                INT_TYPE,
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT_TYPE,
                        JSONStatement(name = "!x", syntaxType = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, syntaxType = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT_TYPE,
                        JSONStatement(name = "!y", syntaxType = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val READ_NOTHING_RETURN_INT_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(INT_TYPE)
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

    val READ_NOTHING_RETURN_DECIMAL_TYPE = JSONStatement(name = Tokens.METHOD_ID, syntaxType = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(syntaxType = SyntaxType.METHOD,
            components = mutableListOf(DECIMAL_TYPE)
        ))
    )

    val LIST_OF_STRING = makeList(STRING_TYPE)

    val AS_TEXT_TYPE = Utils.createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))
    val AS_LIST_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, LIST_OF_STRING))
    val SIZE_TYPE = Utils.createFunctionType(Pair(makeEither(mutableListOf(STRING_TYPE, LIST_OF_ANYTHING_TYPE)),
        INT_TYPE))

    val COLLECTION_OF_NULLABLE_ANYTHING = makeCollection(
        NULLABLE_ANYTHING_TYPE, NULLABLE_ANYTHING_TYPE)
}