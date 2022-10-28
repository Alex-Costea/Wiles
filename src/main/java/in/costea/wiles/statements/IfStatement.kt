package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.ErrorMessages.INTERNAL_ERROR
import `in`.costea.wiles.constants.Predicates.IS_CONTAINED_IN
import `in`.costea.wiles.constants.Tokens.ELSE_ID
import `in`.costea.wiles.constants.Tokens.TERMINATORS
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.statements.expressions.DefaultExpression

class IfStatement(context: Context) : AbstractStatement(context) {

    private val condition = DefaultExpression(context)
    private val thenBlockStatement = CodeBlockStatement(context)
    private var elseBlockStatement : CodeBlockStatement? = null

    override val type: SyntaxType
        get() = SyntaxType.IF

    override fun getComponents(): List<AbstractStatement> {
        elseBlockStatement?:return listOf(condition,thenBlockStatement)
        return listOf(condition,thenBlockStatement,elseBlockStatement!!)
    }

    override fun process(): CompilationExceptionsCollection {
        condition.process().throwFirstIfExists()
        val exceptions = CompilationExceptionsCollection()
        exceptions.addAll(thenBlockStatement.process())
        transmitter.expect(tokenOf(IS_CONTAINED_IN(TERMINATORS)).dontIgnoreNewLine().withErrorMessage(INTERNAL_ERROR))
        if(transmitter.expectMaybe(tokenOf(ELSE_ID)).isPresent) {
            elseBlockStatement = CodeBlockStatement(context)
            exceptions.addAll(elseBlockStatement!!.process())
        }
        return exceptions
    }
}