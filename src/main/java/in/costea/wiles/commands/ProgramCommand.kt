package `in`.costea.wiles.commands

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.ExpressionType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants
import `in`.costea.wiles.statics.Constants.DECLARE_ID

class ProgramCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val components: MutableList<AbstractCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var compiledSuccessfully: Boolean? = null
    override val type: SyntaxType
        get() = SyntaxType.PROGRAM

    override fun getComponents(): List<AbstractCommand> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            while (!transmitter.tokensExhausted()) {
                val command =
                    if(transmitter.expectMaybe(tokenOf(DECLARE_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        AssignmentCommand(transmitter)
                    else ExpressionCommand(transmitter.expect(tokenOf(ExpectParamsBuilder.isContainedIn(Constants.UNARY_OPERATORS))
                        .or(Constants.IS_LITERAL).withErrorMessage("Expression expected!")),
                        transmitter,ExpressionType.RIGHT_SIDE)
                exceptions.addAll(command.process())
                components.add(command)
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }

    fun setCompiledSuccessfully(compiledSuccessfully: Boolean) {
        this.compiledSuccessfully = compiledSuccessfully
    }
}