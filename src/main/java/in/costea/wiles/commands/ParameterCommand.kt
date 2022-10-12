package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ANON_STARTS_WITH
import `in`.costea.wiles.statics.Constants.COLON_ID
import `in`.costea.wiles.statics.Constants.IS_IDENTIFIER

class ParameterCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private var tokenCommand: TokenCommand? = null
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var typeDefinition: TypeDefinitionCommand
    private var isAnon = false
    private set(value)
    {
        name=if(value) "ANON" else ""
        field = value
    }

    init {
        typeDefinition = TypeDefinitionCommand(transmitter)
    }

    override val type: SyntaxType
        get() = SyntaxType.PARAMETER

    override fun getComponents(): List<AbstractCommand> {
        return listOf(tokenCommand!!,
            typeDefinition)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            tokenCommand = TokenCommand(transmitter, transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage("Token expected!")))
            if(tokenCommand!!.token.content.startsWith(ANON_STARTS_WITH))
                isAnon=true
            transmitter.expect(tokenOf(COLON_ID))
            exceptions.addAll(typeDefinition.process())
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}