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