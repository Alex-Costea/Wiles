package wiles.shared

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

interface StatementInterface {
    @get:JsonProperty
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val location : TokenLocation?

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty
    fun getComponents(): MutableList<out StatementInterface>

    @get:JsonProperty
    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    var name: String

    @get:JsonProperty("type")
    val syntaxType: SyntaxType?

    fun getFirstLocation() : TokenLocation
    {
        val location = location
        if(location!= null)
            return location
        else for(part in getComponents())
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