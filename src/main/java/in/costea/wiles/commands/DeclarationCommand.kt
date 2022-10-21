package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.CommandFactory
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.commands.expressions.LeftSideExpressionCommand
import `in`.costea.wiles.commands.expressions.RightSideExpressionCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ASSIGN_ID
import `in`.costea.wiles.statics.Constants.MUTABLE_ID

class DeclarationCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private var left: AbstractCommand? = null
    private var right: AbstractCommand? = null
    private val exceptions = CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractCommand> {
        return listOf(left ?: return emptyList(), right ?: return emptyList())
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if(transmitter.expectMaybe(tokenOf(MUTABLE_ID)).isPresent)
                name = MUTABLE_ID

            val leftExpression = LeftSideExpressionCommand(transmitter)
            this.left = leftExpression
            exceptions.addAll(leftExpression.process())

            transmitter.expect(tokenOf(ASSIGN_ID))

            val rightExpression = CommandFactory(transmitter)
                .addType(RightSideExpressionCommand::class.java)
                .addType(MethodCommand::class.java)
                .create("Right side of declaration expected!")

            this.right = rightExpression
            exceptions.addAll(rightExpression.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}