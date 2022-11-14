package wiles.shared.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import wiles.shared.constants.Settings.ROMANIAN_MODE
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.NOTHING_ID

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
    const val EITHER_ID = "EITHER"

    val REQUIRES_SUBTYPE = setOf(LIST_ID, EITHER_ID)
    val MAX_NR_TYPES = hashMapOf(Pair(LIST_ID,1))
    val MIN_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(EITHER_ID,2))

    init {
        TYPES["!bit"] = BOOLEAN_ID
        TYPES["!byte"] = INT8_ID
        TYPES[if(!ROMANIAN_MODE) "!smallint" else "!întreg_mic"] = INT16_ID
        TYPES[if(!ROMANIAN_MODE) "!int" else "!întreg"] = INT32_ID
        TYPES[if(!ROMANIAN_MODE) "!bigint" else "!întreg_mare"] = INT64_ID
        TYPES["!text"] = STRING_ID
        TYPES[if(!ROMANIAN_MODE) "!rational" else "!rațional"] = DOUBLE_ID
        TYPES[if(!ROMANIAN_MODE) "!list" else "!listă"] = LIST_ID
        TYPES[if(!ROMANIAN_MODE) "!either" else "!ori"] = EITHER_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[METHOD_ID] = METHOD_ID
    }
}