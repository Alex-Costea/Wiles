package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.ANYTHING
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.ExpressionType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.TokenExpectedException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ASSIGN_ID
import `in`.costea.wiles.statics.Constants.DECLARE_ID
import `in`.costea.wiles.statics.Constants.METHOD_ID

class DeclarationCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private var leftExpression : AbstractCommand? = null
    private var rightExpression : AbstractCommand? = null
    private val exceptions=CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractCommand> {
        return listOf(leftExpression?:return emptyList(),rightExpression?:return emptyList())
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            transmitter.expect(tokenOf(DECLARE_ID))

            val leftExpression=ExpressionCommand(transmitter,ExpressionType.LEFT_SIDE)
            this.leftExpression=leftExpression
            exceptions.addAll(leftExpression.process())

            transmitter.expect(tokenOf(ASSIGN_ID))

            //Method declaration
            val rightExpression : AbstractCommand = if(transmitter.expectMaybe(tokenOf(METHOD_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                MethodCommand(transmitter)
            else {
                //Expression
                val message="Expected expression!"
                if (transmitter.expectMaybe(ExpressionCommand.START_OF_EXPRESSION).isPresent)
                    ExpressionCommand(transmitter, ExpressionType.RIGHT_SIDE)
                //Unknown
                else throw TokenExpectedException(message,transmitter.expect(tokenOf(ANYTHING).withErrorMessage(message)).location)
            }
            this.rightExpression=rightExpression
            exceptions.addAll(rightExpression.process())
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}