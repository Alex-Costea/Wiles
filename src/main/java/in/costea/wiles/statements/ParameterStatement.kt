package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.builders.IsWithin
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.Tokens.UNNAMED_START
import `in`.costea.wiles.constants.Tokens.ASSIGN_ID
import `in`.costea.wiles.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import `in`.costea.wiles.constants.Tokens.TYPEOF_ID
import `in`.costea.wiles.constants.Predicates.IS_IDENTIFIER
import `in`.costea.wiles.constants.Predicates.IS_LITERAL
import `in`.costea.wiles.constants.ErrorMessages.LITERAL_EXPECTED_ERROR

class ParameterStatement(transmitter: TokenTransmitter,within: IsWithin) : AbstractStatement(transmitter,within) {
    private var nameToken: TokenStatement? = null
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var typeDefinition: TypeDefinitionStatement
    private var defaultValue: TokenStatement? = null
    private var isAnon = false
        private set(value) {
            name = if (value) "ANON" else ""
            field = value
        }

    init {
        typeDefinition = TypeDefinitionStatement(transmitter,within)
    }

    override val type: SyntaxType
        get() = SyntaxType.PARAMETER

    override fun getComponents(): List<AbstractStatement> {
        val l = mutableListOf(nameToken!!, typeDefinition)
        defaultValue?.let { l.add(it) }
        return l.toList()
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            nameToken = TokenStatement(
                transmitter,
                transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage(IDENTIFIER_EXPECTED_ERROR))
            ,within)
            if (nameToken!!.token.content.startsWith(UNNAMED_START))
                isAnon = true
            transmitter.expect(tokenOf(TYPEOF_ID))
            exceptions.addAll(typeDefinition.process())
            if (transmitter.expectMaybe(tokenOf(ASSIGN_ID)).isPresent) {
                defaultValue = TokenStatement(
                    transmitter,
                    transmitter.expect(tokenOf(IS_LITERAL).withErrorMessage(LITERAL_EXPECTED_ERROR))
                ,within)
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}