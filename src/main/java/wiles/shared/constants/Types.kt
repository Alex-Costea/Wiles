package wiles.shared.constants

import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NOTHING_ID

object Types {
    @JvmField
    val TYPES = hashMapOf<String, String>()

    const val BOOLEAN_ID = "BOOLEAN"
    const val INT64_ID = "INT64"
    const val STRING_ID = "STRING"
    const val DOUBLE_ID = "DOUBLE"
    const val LIST_ID = "LIST"
    const val EITHER_ID = "EITHER"
    const val ANYTHING_ID = "ANYTHING"
    const val METHOD_CALL_ID = "METHOD_CALL"
    const val GENERIC_ID = "GENERIC"
    const val TYPE_TYPE_ID = "TYPE_TYPE"
    const val UNIVERSAL_SUBTYPE_ID = "UNIVERSAL_SUBTYPE"
    const val COLLECTION_ID = "COLLECTION"
    const val DICT_ID = "DICT"

    val REQUIRES_SUBTYPE = setOf(LIST_ID, EITHER_ID, MUTABLE_ID, TYPE_TYPE_ID, COLLECTION_ID, DICT_ID)
    val ALLOWS_GENERICS = hashSetOf(LIST_ID, MUTABLE_ID, TYPE_TYPE_ID, COLLECTION_ID, DICT_ID)
    val MAX_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(MUTABLE_ID,1),Pair(TYPE_TYPE_ID,1),Pair(COLLECTION_ID,2)
        ,Pair(DICT_ID,2))
    val MIN_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(MUTABLE_ID,1),Pair(EITHER_ID,2),
        Pair(TYPE_TYPE_ID,1),Pair(COLLECTION_ID,2),Pair(DICT_ID,2))

    init {
        TYPES["!truth"] = BOOLEAN_ID
        TYPES["!int"] = INT64_ID
        TYPES["!text"] = STRING_ID
        TYPES["!rational"] = DOUBLE_ID
        TYPES["!list"] = LIST_ID
        TYPES["!either"] = EITHER_ID
        TYPES["!anything"] = ANYTHING_ID
        TYPES["!type"] = TYPE_TYPE_ID
        TYPES["!collection"] = COLLECTION_ID
        TYPES["!dict"] = DICT_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[METHOD_ID] = METHOD_ID
        TYPES[MUTABLE_ID] = MUTABLE_ID
    }
}