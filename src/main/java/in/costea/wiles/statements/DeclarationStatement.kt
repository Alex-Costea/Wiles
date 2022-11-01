package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.builders.StatementFactory
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates.IS_IDENTIFIER
import `in`.costea.wiles.constants.Tokens.ASSIGN_ID
import `in`.costea.wiles.constants.Tokens.MUTABLE_ID
import `in`.costea.wiles.constants.Tokens.TYPEDEF_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class DeclarationStatement(context: Context) : AbstractStatement(context) {
    private var left: AbstractStatement? = null
    private var typeStatement : TypeDefinitionStatement? = null
    private var right: AbstractStatement? = null
    private val exceptions = CompilationExceptionsCollection()

    companion object
    {
        val rightExpressionFactory = StatementFactory()
            .addType(DefaultExpression::class.java)
            .addType(MethodStatement::class.java)
    }

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractStatement> {
        val x = mutableListOf(left ?: return emptyList(), right ?: return emptyList())
        if(typeStatement != null)
            x.add(0,typeStatement!!)
        return x
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if(transmitter.expectMaybe(tokenOf(MUTABLE_ID)).isPresent)
                name = MUTABLE_ID

            this.left = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            if(transmitter.expectMaybe(tokenOf(TYPEDEF_ID)).isPresent) {
                typeStatement = TypeDefinitionStatement(context)
                typeStatement!!.process().throwFirstIfExists()
            }
            transmitter.expect(tokenOf(ASSIGN_ID))

            val rightExpression = rightExpressionFactory.setContext(context).create(EXPRESSION_EXPECTED_ERROR)

            this.right = rightExpression
            exceptions.addAll(rightExpression.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}