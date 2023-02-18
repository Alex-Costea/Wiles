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

    const val BOOLEAN_ID = "BOOLEAN"
    const val INT64_ID = "INT64"
    const val STRING_ID = "STRING"
    const val DOUBLE_ID = "DOUBLE"
    const val LIST_ID = "LIST"
    const val EITHER_ID = "EITHER"
    const val MUTABLE_ID = "MUTABLE"
    const val ANYTHING_ID = "ANYTHING"

    val REQUIRES_SUBTYPE = setOf(LIST_ID, EITHER_ID)
    val MAX_NR_TYPES = hashMapOf(Pair(LIST_ID,1))
    val MIN_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(EITHER_ID,2))

    init {
        TYPES[if(!ROMANIAN_MODE)"!truth" else "adevăr"] = BOOLEAN_ID
        TYPES[if(!ROMANIAN_MODE) "!integer" else "!întreg"] = INT64_ID
        TYPES["!text"] = STRING_ID
        TYPES[if(!ROMANIAN_MODE) "!rational" else "!rațional"] = DOUBLE_ID
        TYPES[if(!ROMANIAN_MODE) "!list" else "!listă"] = LIST_ID
        TYPES[if(!ROMANIAN_MODE) "!either" else "!ori"] = EITHER_ID
        TYPES[if(!ROMANIAN_MODE) "!anything" else "!orice"] = ANYTHING_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[METHOD_ID] = METHOD_ID
    }
}