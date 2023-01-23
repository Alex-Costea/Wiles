package wiles.interpreter

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.SyntaxType
import wiles.shared.constants.Utils

data class JSONStatement(
    @JsonProperty var name: String = "",
    @JsonProperty var location: String? = null,
    @JsonProperty val type : SyntaxType? = null,
    @JsonProperty var parsed: Boolean? = null,
    @JsonProperty var components : List<JSONStatement> = listOf()
    )
{
    override fun toString(): String {
        assert(parsed==true)
        return Utils.statementToString(name,type!!,components)
    }
}