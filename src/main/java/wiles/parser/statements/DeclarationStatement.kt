package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.TYPEDEF_ID
import wiles.shared.constants.Tokens.VARIABLE_ID

class DeclarationStatement(context: Context,
                           private val isParam: Boolean = false,
                           private val allowGenerics : Boolean = false)
    : AbstractStatement(context) {
    private var left: TokenStatement? = null
    private var typeStatement : TypeDefinitionStatement? = null
    private var right: DefaultExpression? = null
    private val exceptions = CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): MutableList<AbstractStatement> {
        val x = mutableListOf<AbstractStatement>(left ?: return mutableListOf())
        if(right != null)
            x.add(right!!)
        if(typeStatement != null)
            x.add(0,typeStatement!!)
        return x
    }
    private fun readRight()
    {
        val rightExpression = DefaultExpression(context)
        this.right = rightExpression
        exceptions.addAll(rightExpression.process())
    }
    override fun process(): CompilationExceptionsCollection {
        try {
            if(isParam && transmitter.expectMaybe(tokenOf(ANON_ARG_ID)).isPresent)
                name = ANON_ARG_ID

            if(!isParam && transmitter.expectMaybe(tokenOf(VARIABLE_ID)).isPresent)
                name = VARIABLE_ID

            this.left = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            if(transmitter.expectMaybe(tokenOf(TYPEDEF_ID)).isPresent) {
                typeStatement = TypeDefinitionStatement(context,allowGenerics)
                typeStatement!!.process().throwFirstIfExists()
                if(transmitter.expectMaybe(tokenOf(ASSIGN_ID).dontIgnoreNewLine()).isPresent)
                    readRight()
            }
            else
            {
                transmitter.expect(tokenOf(ASSIGN_ID))
                readRight()
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}