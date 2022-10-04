package `in`.costea.wiles.commands

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.services.TokenTransmitter

@JsonPropertyOrder("compiledSuccessfully", "name", "type", "components")
abstract class AbstractCommand(@JvmField protected val transmitter: TokenTransmitter) {

    @JvmField
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var name = ""

    @get:JsonProperty
    abstract val type: SyntaxType

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty
    abstract fun getComponents(): List< AbstractCommand>

    abstract fun process(): CompilationExceptionsCollection

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