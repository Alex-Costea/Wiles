package wiles.interpreter

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.SyntaxType

data class JSONStatement(
    @JsonProperty var name: String? = null,
    @JsonProperty var location: String? = null,
    @JsonProperty val type : SyntaxType? = null,
    @JsonProperty var compiledSuccessfully: Boolean? = null,
    @JsonProperty var components : List<JSONStatement> = listOf()
    )
{
    override fun toString(): String {
        return "{" +
                (if(name==null) "" else "name=$name, ")+
                (if(type==null) "" else "type=$type, ") +
                (if(compiledSuccessfully==null) "" else "compiledSuccessfully=$compiledSuccessfully, ") +
                (if(components.isEmpty()) "" else "components=$components") +
                "}"
    }
}