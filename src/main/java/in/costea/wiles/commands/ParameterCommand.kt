package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.COLON_ID

class ParameterCommand(transmitter: TokenTransmitter, firstToken: Token) : AbstractCommand(transmitter) {
    private val tokenCommand: TokenCommand
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var typeDefinition: TypeDefinitionCommand

    init {
        tokenCommand = TokenCommand(transmitter, firstToken)
        typeDefinition = TypeDefinitionCommand(transmitter)
    }

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractCommand> {
        return listOf(tokenCommand, typeDefinition)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            transmitter.expect(tokenOf(COLON_ID))
            exceptions.addAll(typeDefinition.process())
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}