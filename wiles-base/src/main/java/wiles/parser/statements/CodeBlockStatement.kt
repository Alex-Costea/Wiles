package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.builders.StatementFactory
import wiles.parser.enums.StatementFactoryTypes
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.UnexpectedEndException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.EXPECT_TERMINATOR
import wiles.shared.constants.Predicates.EXPECT_TERMINATOR_DONT_REMOVE
import wiles.shared.constants.Predicates.READ_REST_OF_LINE
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.START_BLOCK_ID

open class CodeBlockStatement(context: ParserContext) : AbstractStatement(context) {
    companion object{
        val statementFactory = StatementFactory()
            .addType(StatementFactoryTypes.TOP_LEVEL_EXPRESSION)
            .addType(StatementFactoryTypes.DECLARATION_STATEMENT)
            .addType(StatementFactoryTypes.IF_STATEMENT)
            .addType(StatementFactoryTypes.WHILE_STATEMENT)
            .addType(StatementFactoryTypes.FOR_STATEMENT)
            .addType(StatementFactoryTypes.BREAK_STATEMENT)
            .addType(StatementFactoryTypes.RETURN_STATEMENT)
            .addType(StatementFactoryTypes.CONTINUE_STATEMENT)
    }

    private val components: MutableList<AbstractStatement> = ArrayList()
    protected val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    override val syntaxType: SyntaxType
        get() = SyntaxType.CODE_BLOCK

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    private fun expectTerminator() {
        try
        {
            transmitter.expect(EXPECT_TERMINATOR_DONT_REMOVE)
        }
        catch (ignored: UnexpectedEndException) { }
    }

    protected fun readOneStatement(doExpression : Boolean = false) {
        val statement : AbstractStatement
        try
        {
            statement = statementFactory.setContext(context).create()
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
            if(!doExpression)
                expectTerminator()
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
            if (transmitter.expectMaybe(tokenOf(DO_ID)).isPresent) {
                while(transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent)
                    Unit
                readOneStatement(true)
            }
            else {
                transmitter.expect(tokenOf(START_BLOCK_ID))
                while (!transmitter.tokensExhausted()) {
                    if (transmitter.expectMaybe(tokenOf(END_BLOCK_ID)
                            .removeWhen(WhenRemoveToken.Never)).isPresent
                    )
                        break
                    if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) continue
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