package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ASSIGN_ID
import `in`.costea.wiles.statics.Constants.DECLARE_ID
import `in`.costea.wiles.statics.Constants.IS_IDENTIFIER

class AssignmentCommand(transmitter: TokenTransmitter, private val methodAssignment: Boolean) : AbstractCommand(transmitter) {
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
            if(methodAssignment) {
                transmitter.expect(tokenOf(DECLARE_ID))
                leftExpression = TokenCommand(transmitter, transmitter.expect(tokenOf(IS_IDENTIFIER)))
                transmitter.expect(tokenOf(ASSIGN_ID))
                rightExpression=MethodCommand(transmitter)
                exceptions.addAll(rightExpression!!.process())
            }
            else TODO("Non-method assignment")
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}