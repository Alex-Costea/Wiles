package wiles.shared.constants

import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.DATA_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NOTHING_ID

object Types {
    @JvmField
    val TYPES = hashMapOf<String, String>()

    const val BOOLEAN_ID = "BOOLEAN"
    const val INT_ID = "INT"
    const val STRING_ID = "STRING"
    const val DECIMAL_ID = "DECIMAL"
    const val LIST_ID = "LIST"
    const val EITHER_ID = "EITHER"
    const val ANYTHING_ID = "ANYTHING"
    const val TYPE_TYPE_ID = "TYPE_TYPE"
    const val COLLECTION_ID = "COLLECTION"
    const val DICT_ID = "DICT"

    val REQUIRES_SUBTYPE = setOf(LIST_ID, MUTABLE_ID, TYPE_TYPE_ID, COLLECTION_ID, DICT_ID, EITHER_ID, CONST_ID)
    val MAX_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(MUTABLE_ID,1),Pair(TYPE_TYPE_ID,1),Pair(COLLECTION_ID,2)
        ,Pair(DICT_ID,2), Pair(CONST_ID, 1))
    val MIN_NR_TYPES = hashMapOf(Pair(LIST_ID,1),Pair(MUTABLE_ID,1), Pair(CONST_ID, 1),
        Pair(TYPE_TYPE_ID,1),Pair(COLLECTION_ID,2),Pair(DICT_ID,2), Pair(EITHER_ID, 2))

    init {
        TYPES["!truth"] = BOOLEAN_ID
        TYPES["!int"] = INT_ID
        TYPES["!text"] = STRING_ID
        TYPES["!decimal"] = DECIMAL_ID
        TYPES["!list"] = LIST_ID
        TYPES["!anything"] = ANYTHING_ID
        TYPES["!type"] = TYPE_TYPE_ID
        TYPES["!collection"] = COLLECTION_ID
        TYPES["!dict"] = DICT_ID
        TYPES["!either"] = EITHER_ID
        TYPES[NOTHING_ID] = NOTHING_ID
        TYPES[MAYBE_ID] = MAYBE_ID
        TYPES[FUNC_ID] = FUNC_ID
        TYPES[MUTABLE_ID] = MUTABLE_ID
        TYPES[DATA_ID] = DATA_ID
        TYPES[CONST_ID] = CONST_ID
    }
}