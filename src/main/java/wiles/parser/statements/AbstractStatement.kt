package wiles.parser.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Utils

@JsonPropertyOrder("parsed", "name", "type", "location", "components")
abstract class AbstractStatement(val context: Context)
{
    @JvmField
    protected val transmitter = context.transmitter
    @JvmField
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var name = ""

    @get:JsonProperty
    abstract val type: SyntaxType

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected var location: String? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty
    abstract fun getComponents(): List<AbstractStatement>

    abstract fun process(): CompilationExceptionsCollection

    override fun toString(): String {
        return Utils.statementToString(name,type,getComponents())
    }
}