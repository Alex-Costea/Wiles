package wiles.parser.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import wiles.parser.builders.Context
import wiles.parser.constants.Predicates.EXPECT_TERMINATOR_DONT_REMOVE
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.UnexpectedEndException

@JsonPropertyOrder("compiledSuccessfully", "name", "type", "components")
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty
    abstract fun getComponents(): List<AbstractStatement>

    abstract fun process(): CompilationExceptionsCollection

    open fun handleEndOfStatement()
    {
        try
        {
            transmitter.expect(EXPECT_TERMINATOR_DONT_REMOVE)
        }
        catch (ignored : UnexpectedEndException){}
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(type)
        if (name != "") sb.append(" ").append(name)
        if (getComponents().isNotEmpty()) {
            sb.append("(")
            for ((i, component) in getComponents().withIndex()) {
                sb.append(component.toString())
                if (i < getComponents().size - 1) sb.append("; ")
            }
            sb.append(")")
        }
        return sb.toString()
    }
}