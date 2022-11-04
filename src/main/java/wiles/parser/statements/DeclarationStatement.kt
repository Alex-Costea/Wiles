package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.parser.constants.Predicates.IS_IDENTIFIER
import wiles.parser.constants.Tokens.ASSIGN_ID
import wiles.parser.constants.Tokens.MUTABLE_ID
import wiles.parser.constants.Tokens.TYPEDEF_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.statements.expressions.DefaultExpression

class DeclarationStatement(context: Context) : AbstractStatement(context) {
    private var left: AbstractStatement? = null
    private var typeStatement : TypeDefinitionStatement? = null
    private var right: DefaultExpression? = null
    private val exceptions = CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractStatement> {
        val x = mutableListOf(left ?: return emptyList(), right ?: return emptyList())
        if(typeStatement != null)
            x.add(0,typeStatement!!)
        return x
    }

    override fun handleEndOfStatement() {
        (right?.handleEndOfStatement())?:super.handleEndOfStatement()
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

            val rightExpression = DefaultExpression(context)

            this.right = rightExpression
            exceptions.addAll(rightExpression.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}