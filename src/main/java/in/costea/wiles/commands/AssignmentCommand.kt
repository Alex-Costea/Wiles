package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.ExpressionType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ASSIGNMENT_START_ID
import `in`.costea.wiles.statics.Constants.ASSIGN_ID

class AssignmentCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private var leftExpression : AbstractCommand? = null
    private var rightExpression : AbstractCommand? = null
    private val exceptions=CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.ASSIGNMENT

    override fun getComponents(): List<AbstractCommand> {
        return listOf(leftExpression?:return emptyList(),rightExpression?:return emptyList())
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            transmitter.expect(ExpectParamsBuilder.tokenOf(ASSIGNMENT_START_ID))

            val leftExpression=ExpressionCommand(transmitter, ExpressionType.LEFT_SIDE)
            this.leftExpression=leftExpression
            exceptions.addAll(leftExpression.process())

            transmitter.expect(ExpectParamsBuilder.tokenOf(ASSIGN_ID))

            val rightExpression=ExpressionCommand(transmitter, ExpressionType.RIGHT_SIDE)
            this.rightExpression=rightExpression
            exceptions.addAll(rightExpression.process())
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}