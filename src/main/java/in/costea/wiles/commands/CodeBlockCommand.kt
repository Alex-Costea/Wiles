package `in`.costea.wiles.commands

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.ANYTHING
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.isContainedIn
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.ASSIGNMENT_START_ID
import `in`.costea.wiles.statics.Constants.DECLARE_ID
import `in`.costea.wiles.statics.Constants.DO_ID
import `in`.costea.wiles.statics.Constants.END_BLOCK_ID
import `in`.costea.wiles.statics.Constants.NOTHING_ID
import `in`.costea.wiles.statics.Constants.START_BLOCK_ID
import `in`.costea.wiles.statics.Constants.STATEMENT_TERMINATORS
import `in`.costea.wiles.statics.Constants.TOKENS_INVERSE

class CodeBlockCommand(transmitter: TokenTransmitter,private val outerMost:Boolean) : AbstractCommand(transmitter) {
    private val components: MutableList<AbstractCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private var compiledSuccessfully: Boolean? = null

    override val type: SyntaxType
        get() = SyntaxType.CODE_BLOCK

    override fun getComponents(): List<AbstractCommand> {
        return components
    }

    @Throws(AbstractCompilationException::class)
    private fun readOneStatement() {
        if (transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent) return

        val command: AbstractCommand = if (transmitter.expectMaybe(ExpressionCommand.START_OF_EXPRESSION).isPresent)
            RightSideExpressionCommand(transmitter)
        else if(transmitter.expectMaybe(tokenOf(DECLARE_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
            DeclarationCommand(transmitter)
        else if(transmitter.expectMaybe(tokenOf(ASSIGNMENT_START_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
            AssignmentCommand(transmitter)
        else {
            //token should always exist at this location
            val (content, location) = transmitter.expectMaybe(tokenOf(ANYTHING)).get()
            throw UnexpectedTokenException(TOKENS_INVERSE[content]?:content, location)
        }

        val newExceptions = command.process()
        if (newExceptions.size > 0) throw newExceptions[0]
        components.add(command)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if (!outerMost && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent) {
                if (transmitter.expectMaybe(tokenOf(NOTHING_ID)).isEmpty)
                    readOneStatement()
                return exceptions
            } else {
                if(!outerMost) transmitter.expect(tokenOf(START_BLOCK_ID))
                while (!transmitter.tokensExhausted()) {
                    if (!outerMost && transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    readOneStatement()
                }
                if(!outerMost) transmitter.expect(tokenOf(END_BLOCK_ID))
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        compiledSuccessfully=exceptions.isEmpty()
        return exceptions
    }
}