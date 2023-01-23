package wiles.shared

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.constants.Utils

data class JSONStatement(
    @JsonProperty var name: String = "",
    @JsonProperty var location: String? = null,
    @JsonProperty val type : SyntaxType? = null,
    @JsonProperty var parsed: Boolean? = null,
    @JsonProperty var components : MutableList<JSONStatement> = mutableListOf()
)
{
    override fun toString(): String {
        assert(parsed==true)
        return Utils.statementToString(name,type!!,components)
    }
}