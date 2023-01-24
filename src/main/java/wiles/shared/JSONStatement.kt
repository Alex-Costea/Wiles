package wiles.shared

import com.fasterxml.jackson.annotation.JsonProperty
import wiles.shared.constants.Utils

data class JSONStatement(
    @JsonProperty var name: String = "",
    @JsonProperty var location: TokenLocation? = null,
    @JsonProperty val type : SyntaxType? = null,
    @JsonProperty var parsed: Boolean? = null,
    @JsonProperty var components : MutableList<JSONStatement> = mutableListOf()
)
{
    override fun toString(): String {
        assert(parsed==true)
        return Utils.statementToString(name,type!!,components)
    }

    fun getFirstLocation() : TokenLocation
    {
        val location = location
        if(location!= null)
            return location
        else for(part in components)
        {
            try
            {
                return part.getFirstLocation()
            }
            catch (_: InternalErrorException) {}
        }
        throw InternalErrorException("No token locations found!")
    }
}