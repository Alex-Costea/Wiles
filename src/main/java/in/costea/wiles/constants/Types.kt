package `in`.costea.wiles.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import `in`.costea.wiles.constants.Settings.ROMANIAN_MODE
import `in`.costea.wiles.constants.Tokens.MAYBE_ID
import `in`.costea.wiles.constants.Tokens.METHOD_ID
import `in`.costea.wiles.constants.Tokens.NOTHING_ID
import `in`.costea.wiles.constants.Tokens.TIMES_ID

object Types {
    @JvmField
    val TYPES: BiMap<String, String> = HashBiMap.create()

    const val BOOLEAN_ID = "BOOLEAN"
    const val INT8_ID = "INT8"
    const val INT16_ID = "INT16"
    const val INT32_ID = "INT32"
    const val INT64_ID = "INT64"
    const val STRING_ID = "STRING"
    const val DOUBLE_ID = "DOUBLE"
    const val LIST_ID = "LIST"
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
        TYPES[TIMES_ID] =  GENERIC_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[METHOD_ID] = METHOD_ID
    }
}