package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.builders.StatementFactory
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates.EXPECT_TERMINATOR
import `in`.costea.wiles.constants.Tokens.ELSE_ID
import `in`.costea.wiles.constants.Tokens.TERMINATOR_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class IfStatement(context: Context) : AbstractStatement(context) {

    private var handledEOL = true

    private val condition = DefaultExpression(context)
    private val thenBlockStatement = CodeBlockStatement(context)
    private var elseBlockStatement : AbstractStatement? = null

    override val type: SyntaxType
        get() = SyntaxType.IF

    override fun getComponents(): List<AbstractStatement> {
        elseBlockStatement?:return listOf(condition,thenBlockStatement)
        return listOf(condition,thenBlockStatement,elseBlockStatement!!)
    }

    override fun handleEndOfStatement()
    {
        if(!handledEOL)
            super.handleEndOfStatement()
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            exceptions.addAll(condition.process())
            exceptions.addAll(thenBlockStatement.process())
            val tempToken = transmitter.expectMaybe(EXPECT_TERMINATOR)
            if (!tempToken.isPresent)
                return exceptions
            val params = tokenOf(ELSE_ID)
            if (tempToken.get().content == TERMINATOR_ID)
                params.dontIgnoreNewLine()
            if (transmitter.expectMaybe(params).isPresent) {
                elseBlockStatement = StatementFactory().setContext(context)
                    .addType(CodeBlockStatement::class.java)
                    .addType(IfStatement::class.java)
                    .create(EXPRESSION_EXPECTED_ERROR)
                exceptions.addAll(elseBlockStatement!!.process())
                handledEOL = false
            }
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}