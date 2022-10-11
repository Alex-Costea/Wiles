package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.ANYTHING
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.ExpressionType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.TokenExpectedException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants
import `in`.costea.wiles.statics.Constants.ASSIGN_ID
import `in`.costea.wiles.statics.Constants.DECLARE_ID
import `in`.costea.wiles.statics.Constants.METHOD_ID

class AssignmentCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
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
            val firstToken = transmitter.expect(tokenOf(ExpectParamsBuilder.isContainedIn(Constants.UNARY_OPERATORS))
                .or(Constants.IS_LITERAL).withErrorMessage("Expression expected in left side of assignment!"))
            val leftExpression=ExpressionCommand(firstToken,transmitter,ExpressionType.LEFT_SIDE)
            this.leftExpression=leftExpression
            exceptions.addAll(leftExpression.process())
            transmitter.expect(tokenOf(ASSIGN_ID))

            //Method declaration
            val rightExpression : AbstractCommand = if(transmitter.expectMaybe(tokenOf(METHOD_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                MethodCommand(transmitter)
            else {
                //Expression
                val optionalToken = transmitter.expectMaybe(tokenOf(ExpectParamsBuilder.isContainedIn(Constants.UNARY_OPERATORS)).or(Constants.IS_LITERAL))
                if (optionalToken.isPresent)
                    ExpressionCommand(optionalToken.get(), transmitter, ExpressionType.RIGHT_SIDE)
                //Unknown
                else throw TokenExpectedException("Expected expression!",transmitter.expect(tokenOf(ANYTHING)).location)
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