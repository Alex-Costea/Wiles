package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.isContainedIn
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.ExpressionType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.DECLARE_ID
import `in`.costea.wiles.statics.Constants.DO_ID
import `in`.costea.wiles.statics.Constants.END_BLOCK_ID
import `in`.costea.wiles.statics.Constants.IS_LITERAL
import `in`.costea.wiles.statics.Constants.NOTHING_ID
import `in`.costea.wiles.statics.Constants.START_BLOCK_ID
import `in`.costea.wiles.statics.Constants.STATEMENT_TERMINATORS
import `in`.costea.wiles.statics.Constants.TOKENS_INVERSE
import `in`.costea.wiles.statics.Constants.UNARY_OPERATORS

class CodeBlockCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val components: MutableList<AbstractCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val type: SyntaxType
        get() = SyntaxType.CODE_BLOCK

    override fun getComponents(): List<AbstractCommand> {
        return components
    }

    @Throws(AbstractCompilationException::class)
    private fun readOneStatement() {
        if (transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent) return
        val command: AbstractCommand
        val optionalToken = transmitter.expectMaybe(tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL))
        command = if (optionalToken.isPresent)
            ExpressionCommand(optionalToken.get(), transmitter, ExpressionType.RIGHT_SIDE)
        else if(transmitter.expectMaybe(tokenOf(DECLARE_ID).removeWhen(WhenRemoveToken.Never)).isPresent) {
            AssignmentCommand(transmitter)
        } else {
            val (content, location) = transmitter.expect(tokenOf(ExpectParamsBuilder.ANYTHING))
            throw UnexpectedTokenException(TOKENS_INVERSE[content]!!, location)
        }
        val newExceptions: CompilationExceptionsCollection = command.process()
        if (newExceptions.size > 0) throw newExceptions[0]
        components.add(command)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if (transmitter.expectMaybe(tokenOf(DO_ID)).isPresent) {
                if (transmitter.expectMaybe(tokenOf(NOTHING_ID)).isEmpty)
                    readOneStatement()
                return exceptions
            } else {
                transmitter.expect(tokenOf(START_BLOCK_ID))
                while (!transmitter.tokensExhausted()) {
                    if (transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    readOneStatement()
                }
                transmitter.expect(tokenOf(END_BLOCK_ID))
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}