package `in`.costea.wiles.commands

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter

class ProgramCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val components: MutableList<AssignmentCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var compiledSuccessfully: Boolean? = null
    override val type: SyntaxType
        get() = SyntaxType.PROGRAM

    override fun getComponents(): List<AssignmentCommand> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            while (!transmitter.tokensExhausted()) {
                val assignmentCommand = AssignmentCommand(transmitter,true)
                exceptions.addAll(assignmentCommand.process())
                components.add(assignmentCommand)
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