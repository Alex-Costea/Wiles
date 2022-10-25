package `in`.costea.wiles.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.builders.StatementFactory
import `in`.costea.wiles.constants.ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates
import `in`.costea.wiles.constants.Predicates.EXPECT_TERMINATOR
import `in`.costea.wiles.constants.Predicates.READ_REST_OF_LINE
import `in`.costea.wiles.constants.Tokens
import `in`.costea.wiles.constants.Tokens.DO_ID
import `in`.costea.wiles.constants.Tokens.END_BLOCK_ID
import `in`.costea.wiles.constants.Tokens.START_BLOCK_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.UnexpectedEndException
import `in`.costea.wiles.statements.expressions.TopLevelExpression

class CodeBlockStatement(context: Context) : AbstractStatement(context) {
    private val components: MutableList<AbstractStatement> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private var compiledSuccessfully: Boolean? = null

    override val type: SyntaxType
        get() = SyntaxType.CODE_BLOCK

    override fun getComponents(): List<AbstractStatement> {
        return components
    }

    private fun readOneStatement() {
        if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) return
        val statementFactory = StatementFactory(context.setOutermost(false))
            .addType(TopLevelExpression::class.java)
            .addType(DeclarationStatement::class.java)
            .addType(IfStatement::class.java)
            .addType(ForStatement::class.java)
            .addType(WhileStatement::class.java)
            .addType(BreakStatement::class.java)
            .addType(ReturnStatement::class.java)
            .addType(ContinueStatement::class.java)
        val statement : AbstractStatement
        try
        {
            statement = statementFactory.create()
        }
        catch (ex : AbstractCompilationException)
        {
            exceptions.add(ex)
            readRestOfLine()
            return
        }
        val newExceptions = statement.process()
        exceptions.addAll(newExceptions)
        if(!newExceptions.isEmpty()) {
            readRestOfLine()
            components.add(statement)
        }
        else try {
            transmitter.expect(tokenOf(Predicates.IS_CONTAINED_IN(Tokens.TERMINATORS)).dontIgnoreNewLine()
                .withErrorMessage(END_OF_STATEMENT_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never))
        }
        catch(ignored : UnexpectedEndException){}
        catch(ex : AbstractCompilationException)
        {
            readRestOfLine()
            exceptions.add(ex)
        }
        finally {
            components.add(statement)
        }
    }

    private fun readRestOfLine() {
        while(transmitter.expectMaybe(READ_REST_OF_LINE).isPresent)
            Unit
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if (!context.isOutermost && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent)
                readOneStatement()
            else {
                if (!context.isOutermost) {
                    transmitter.expect(tokenOf(START_BLOCK_ID))
                    transmitter.expect(EXPECT_TERMINATOR)
                }
                while (!transmitter.tokensExhausted()) {
                    if (!context.isOutermost && transmitter.expectMaybe(tokenOf(END_BLOCK_ID)
                            .removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    readOneStatement()
                }
                if (!context.isOutermost)
                    transmitter.expect(tokenOf(END_BLOCK_ID))
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        compiledSuccessfully = exceptions.isEmpty()
        return exceptions
    }
}