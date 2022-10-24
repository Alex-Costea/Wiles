package `in`.costea.wiles.constants

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import `in`.costea.wiles.constants.Settings.ROMANIAN_MODE
import `in`.costea.wiles.constants.Tokens.NOTHING_ID

object Types {
    @JvmField
    val TYPES: BiMap<String, String> = HashBiMap.create()

    init {
        TYPES["!bit"] = "BOOLEAN"
        TYPES["!byte"] = "INT8"
        TYPES[if(!ROMANIAN_MODE) "!smallint" else "!întreg_mic"] = "INT16"
        TYPES[if(!ROMANIAN_MODE) "!int" else "!întreg"] = "INT32"
        TYPES[if(!ROMANIAN_MODE) "!bigint" else "!întreg_mare"] = "INT64"
        TYPES["!text"] = "STRING"
        TYPES[if(!ROMANIAN_MODE) "!rational" else "!rațional"] = "DOUBLE"
        TYPES[if(!ROMANIAN_MODE) "!list" else "!listă"] = "ARRAY_LIST"
        TYPES[if(!ROMANIAN_MODE) "!either" else "!ori"] = "EITHER"
        TYPES[if(!ROMANIAN_MODE) "!range" else "!interval"] = "RANGE"
        TYPES[NOTHING_ID] = NOTHING_ID
    }
}