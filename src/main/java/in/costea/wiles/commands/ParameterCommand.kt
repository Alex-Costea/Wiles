package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.UNNAMED_START
import `in`.costea.wiles.statics.Constants.ASSIGN_ID
import `in`.costea.wiles.statics.Constants.IDENTIFIER_EXPECTED_ERROR
import `in`.costea.wiles.statics.Constants.TYPEOF_ID
import `in`.costea.wiles.statics.Constants.IS_IDENTIFIER
import `in`.costea.wiles.statics.Constants.IS_LITERAL
import `in`.costea.wiles.statics.Constants.LITERAL_EXPECTED_ERROR

class ParameterCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private var nameToken: TokenCommand? = null
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var typeDefinition: TypeDefinitionCommand
    private var defaultValue: TokenCommand? = null
    private var isAnon = false
        private set(value) {
            name = if (value) "ANON" else ""
            field = value
        }

    init {
        typeDefinition = TypeDefinitionCommand(transmitter)
    }

    override val type: SyntaxType
        get() = SyntaxType.PARAMETER

    override fun getComponents(): List<AbstractCommand> {
        val l = mutableListOf(nameToken!!, typeDefinition)
        defaultValue?.let { l.add(it) }
        return l.toList()
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            nameToken = TokenCommand(
                transmitter,
                transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage(IDENTIFIER_EXPECTED_ERROR))
            )
            if (nameToken!!.token.content.startsWith(UNNAMED_START))
                isAnon = true
            transmitter.expect(tokenOf(TYPEOF_ID))
            exceptions.addAll(typeDefinition.process())
            if (transmitter.expectMaybe(tokenOf(ASSIGN_ID)).isPresent) {
                defaultValue = TokenCommand(
                    transmitter,
                    transmitter.expect(tokenOf(IS_LITERAL).withErrorMessage(LITERAL_EXPECTED_ERROR))
                )
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}