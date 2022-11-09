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
import wiles.parser.statements.expressions.TopLevelExpression

class CodeBlockStatement(context: Context) : AbstractStatement(context) {
    companion object{
        val statementFactory = StatementFactory()
            .addType(TopLevelExpression::class.java)
            .addType(DeclarationStatement::class.java)
            .addType(WhenStatement::class.java)
            .addType(WhileStatement::class.java)
            .addType(ForStatement::class.java)
            .addType(BreakStatement::class.java)
            .addType(ReturnStatement::class.java)
            .addType(ContinueStatement::class.java)
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

    private fun readOneStatement(doExpression : Boolean = false) {
        val statement : AbstractStatement
        try
        {
            var newContext = context.setOutermost(false)
            if(!doExpression)
                newContext = newContext.setWithinInnerExpression(false)
            statement = statementFactory.setContext(newContext).create()
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
            if(!(doExpression && context.isWithinInnerExpression))
                statement.handleEndOfStatement()
        }
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
                readOneStatement(true)
            }
            else {
                if (!context.isOutermost) {
                    transmitter.expect(tokenOf(START_BLOCK_ID))
                    try {
                        transmitter.expect(EXPECT_TERMINATOR)
                    }
                    catch(_ : UnexpectedEndException){}
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
                        if(!context.isWithinInnerExpression)
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