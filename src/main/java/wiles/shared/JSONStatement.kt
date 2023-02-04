package wiles.shared

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.constants.Utils

class JSONStatement(
    @JsonProperty override var name: String = "",
    @JsonProperty override var location: TokenLocation? = null,
    @JsonProperty override val type : SyntaxType? = null,
    @JsonProperty var parsed: Boolean? = null,
    @JvmField @JsonProperty var components : MutableList<JSONStatement> = mutableListOf()
) : StatementInterface
{
    override fun toString(): String {
        return Utils.statementToString(name,type!!,components)
    }

    override fun getComponents(): MutableList<JSONStatement> {
        return components.toMutableList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JSONStatement

        if (name != other.name) return false
        if (type != other.type) return false
        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + components.hashCode()
        return result
    }

}