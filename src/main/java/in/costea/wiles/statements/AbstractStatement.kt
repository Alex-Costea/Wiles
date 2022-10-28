package `in`.costea.wiles.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.constants.ErrorMessages
import `in`.costea.wiles.constants.Predicates
import `in`.costea.wiles.constants.Tokens
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken

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
        transmitter.expect(
            ExpectParamsBuilder.tokenOf(Predicates.IS_CONTAINED_IN(Tokens.TERMINATORS)).dontIgnoreNewLine()
            .withErrorMessage(ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never))
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