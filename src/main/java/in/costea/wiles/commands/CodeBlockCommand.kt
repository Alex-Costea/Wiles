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
import `in`.costea.wiles.statics.Constants.DECLARE_METHOD_ID
import `in`.costea.wiles.statics.Constants.DO_ID
import `in`.costea.wiles.statics.Constants.END_BLOCK_ID
import `in`.costea.wiles.statics.Constants.IS_LITERAL
import `in`.costea.wiles.statics.Constants.NOTHING_ID
import `in`.costea.wiles.statics.Constants.ROUND_BRACKET_START_ID
import `in`.costea.wiles.statics.Constants.START_BLOCK_ID
import `in`.costea.wiles.statics.Constants.STATEMENT_TERMINATORS
import `in`.costea.wiles.statics.Constants.TOKENS_INVERSE
import `in`.costea.wiles.statics.Constants.UNARY_OPERATORS

class CodeBlockCommand(transmitter: TokenTransmitter, private val standAlone: Boolean) : AbstractCommand(transmitter) {
    private val components: MutableList<ExpressionCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val type: SyntaxType
        get() = SyntaxType.CODE_BLOCK

    override fun getComponents(): List<ExpressionCommand> {
        return components
    }

    @Throws(AbstractCompilationException::class)
    private fun readOneStatement() {
        if (transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent) return
        val expressionCommand: ExpressionCommand
        val innerExpression = transmitter.expectMaybe(tokenOf(ROUND_BRACKET_START_ID)).isPresent
        var optionalToken = transmitter.expectMaybe(tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL))
        if (optionalToken.isPresent) expressionCommand = ExpressionCommand(
            optionalToken.get(),
            transmitter,
            if (innerExpression) ExpressionType.INSIDE_ROUND else ExpressionType.REGULAR
        ) else {
            optionalToken = transmitter.expectMaybe(tokenOf(DECLARE_METHOD_ID))
            if (standAlone && optionalToken.isPresent) throw UnexpectedTokenException(
                "Cannot declare method in body-only mode!",
                optionalToken.get().location
            ) else {
                val (content, location) = transmitter.expect(tokenOf(ExpectParamsBuilder.ANYTHING))
                throw UnexpectedTokenException(TOKENS_INVERSE[content]!!, location)
            }
        }
        val newExceptions: CompilationExceptionsCollection = expressionCommand.process()
        if (newExceptions.size > 0) throw newExceptions[0]
        components.add(expressionCommand)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if (!standAlone && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent) {
                if (transmitter.expectMaybe(tokenOf(NOTHING_ID)).isEmpty)
                    readOneStatement()
                return exceptions
            } else {
                if (!standAlone) transmitter.expect(tokenOf(START_BLOCK_ID))
                while (!transmitter.tokensExhausted()) {
                    if (!standAlone && transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    readOneStatement()
                }
                if (!standAlone) transmitter.expect(tokenOf(END_BLOCK_ID))
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}