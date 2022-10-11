package `in`.costea.wiles.commands

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.METHOD_ID

class ProgramCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val components: MutableList<MethodCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var compiledSuccessfully: Boolean? = null
    override val type: SyntaxType
        get() = SyntaxType.PROGRAM

    override fun getComponents(): List<MethodCommand> {
        return components
    }

    fun addMethod(command: MethodCommand) {
        components.add(command)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            while (!transmitter.tokensExhausted()) {
                transmitter.expect(tokenOf(METHOD_ID))
                val methodCommand = MethodCommand(transmitter)
                exceptions.addAll(methodCommand.process())
                components.add(methodCommand)
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