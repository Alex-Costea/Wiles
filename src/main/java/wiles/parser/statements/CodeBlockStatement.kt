package wiles.parser.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.StatementFactory
import wiles.parser.constants.Predicates.EXPECT_TERMINATOR
import wiles.parser.constants.Predicates.EXPECT_TERMINATOR_DONT_REMOVE
import wiles.parser.constants.Predicates.READ_REST_OF_LINE
import wiles.parser.constants.Tokens.DO_ID
import wiles.parser.constants.Tokens.END_BLOCK_ID
import wiles.parser.constants.Tokens.START_BLOCK_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.exceptions.UnexpectedEndException

class CodeBlockStatement(context: Context) : AbstractStatement(context) {
    companion object{
        val statementFactory = StatementFactory()
            .addType(wiles.parser.statements.expressions.TopLevelExpression::class.java)
            .addType(DeclarationStatement::class.java)
            .addType(WhenStatement::class.java)
            .addType(WhileStatement::class.java)
            .addType(wiles.parser.statements.BreakStatement::class.java)
            .addType(ReturnStatement::class.java)
            .addType(wiles.parser.statements.ContinueStatement::class.java)
    }

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
        val statement : AbstractStatement
        try
        {
            statement = statementFactory.setContext(context.setOutermost(false)).create()
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
            statement.handleEndOfStatement()
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
            if (!context.isOutermost && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent) {
                while(transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent)
                    Unit
                readOneStatement()
            }
            else {
                if (!context.isOutermost) {
                    transmitter.expect(tokenOf(START_BLOCK_ID))
                    transmitter.expect(EXPECT_TERMINATOR)
                }
                while (!transmitter.tokensExhausted()) {
                    if (!context.isOutermost && transmitter.expectMaybe(tokenOf(END_BLOCK_ID)
                            .removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) continue
                    readOneStatement()
                }
                if (!context.isOutermost) {
                    transmitter.expect(tokenOf(END_BLOCK_ID))
                    try {
                        transmitter.expect(EXPECT_TERMINATOR_DONT_REMOVE)
                    }
                    catch(_: UnexpectedEndException) {}
                }
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        compiledSuccessfully = exceptions.isEmpty()
        return exceptions
    }
}