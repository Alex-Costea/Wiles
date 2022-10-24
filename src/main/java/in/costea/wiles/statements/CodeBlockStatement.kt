package `in`.costea.wiles.statements

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import `in`.costea.wiles.builders.StatementFactory
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.statements.expressions.AssignableExpression
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.builders.CodeBlockType
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.Tokens.DO_ID
import `in`.costea.wiles.constants.Tokens.END_BLOCK_ID
import `in`.costea.wiles.constants.Predicates.EXPECT_TERMINATOR
import `in`.costea.wiles.constants.Tokens.START_BLOCK_ID

class CodeBlockStatement(transmitter: TokenTransmitter, private val blockType: CodeBlockType) : AbstractStatement(transmitter) {
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

    @Throws(AbstractCompilationException::class)
    private fun readOneStatement() {
        if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) return
        val statementFactory= StatementFactory(transmitter)
            .addType(AssignableExpression::class.java)
            .addType(DeclarationStatement::class.java)
        if(blockType.isWithinMethod)
            statementFactory.addType(ReturnStatement::class.java)
        val statement = statementFactory.create()
        statement.process().throwFirstIfExists()
        components.add(statement)
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if (!blockType.isOutermost && transmitter.expectMaybe(tokenOf(DO_ID)).isPresent)
                readOneStatement()
            else {
                if (!blockType.isOutermost) transmitter.expect(tokenOf(START_BLOCK_ID))
                while (!transmitter.tokensExhausted()) {
                    if (!blockType.isOutermost && transmitter.expectMaybe(tokenOf(END_BLOCK_ID)
                            .removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    readOneStatement()
                }
                if (!blockType.isOutermost) transmitter.expect(tokenOf(END_BLOCK_ID))
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        compiledSuccessfully = exceptions.isEmpty()
        return exceptions
    }
}