package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.StatementFactory
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.statements.expressions.DefaultExpression
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.Tokens.ASSIGN_ID
import `in`.costea.wiles.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates.IS_IDENTIFIER
import `in`.costea.wiles.constants.Tokens.MUTABLE_ID
import `in`.costea.wiles.constants.ErrorMessages.RIGHT_SIDE_EXPECTED_ERROR

class DeclarationStatement(transmitter: TokenTransmitter) : AbstractStatement(transmitter) {
    private var left: AbstractStatement? = null
    private var right: AbstractStatement? = null
    private val exceptions = CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractStatement> {
        return listOf(left ?: return emptyList(), right ?: return emptyList())
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if(transmitter.expectMaybe(tokenOf(MUTABLE_ID)).isPresent)
                name = MUTABLE_ID

            this.left = TokenStatement(transmitter,transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)))

            transmitter.expect(tokenOf(ASSIGN_ID))

            val rightExpression = StatementFactory(transmitter)
                .addType(DefaultExpression::class.java)
                .addType(MethodStatement::class.java)
                .create(RIGHT_SIDE_EXPECTED_ERROR)

            this.right = rightExpression
            exceptions.addAll(rightExpression.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}