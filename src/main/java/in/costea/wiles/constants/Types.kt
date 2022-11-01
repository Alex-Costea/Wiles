package `in`.costea.wiles.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import `in`.costea.wiles.constants.Settings.ROMANIAN_MODE
import `in`.costea.wiles.constants.Tokens.MAYBE_ID
import `in`.costea.wiles.constants.Tokens.METHOD_ID
import `in`.costea.wiles.constants.Tokens.NOTHING_ID

object Types {
    @JvmField
    val TYPES: BiMap<String, String> = HashBiMap.create()

    private const val BOOLEAN_ID = "BOOLEAN"
    private const val INT8_ID = "INT8"
    private const val INT16_ID = "INT16"
    private const val INT32_ID = "INT32"
    private const val INT64_ID = "INT64"
    private const val STRING_ID = "STRING"
    private const val DOUBLE_ID = "DOUBLE"
    private const val LIST_ID = "LIST"
    private const val ANYTHING_ID = "ANYTHING"
    const val GENERIC_ID = "GENERIC"


    val REQUIRES_SUBTYPE = setOf(LIST_ID)

    init {
        TYPES["!bit"] = BOOLEAN_ID
        TYPES["!byte"] = INT8_ID
        TYPES[if(!ROMANIAN_MODE) "!smallint" else "!întreg_mic"] = INT16_ID
        TYPES[if(!ROMANIAN_MODE) "!int" else "!întreg"] = INT32_ID
        TYPES[if(!ROMANIAN_MODE) "!bigint" else "!întreg_mare"] = INT64_ID
        TYPES["!text"] = STRING_ID
        TYPES[if(!ROMANIAN_MODE) "!rational" else "!rațional"] = DOUBLE_ID
        TYPES[if(!ROMANIAN_MODE) "!list" else "!listă"] = LIST_ID
        TYPES[if(!ROMANIAN_MODE) "!anything" else "!orice"] = ANYTHING_ID
        TYPES["!"] =  GENERIC_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[METHOD_ID] = METHOD_ID
    }
}