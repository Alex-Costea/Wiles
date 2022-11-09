package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.parser.constants.Predicates.IS_IDENTIFIER
import wiles.parser.constants.Tokens.ANON_ARG_ID
import wiles.parser.constants.Tokens.ASSIGN_ID
import wiles.parser.constants.Tokens.MUTABLE_ID
import wiles.parser.constants.Tokens.TYPEDEF_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.statements.expressions.DefaultExpression

class DeclarationStatement(context: Context, private val isParam: Boolean = false) : AbstractStatement(context) {
    private var left: AbstractStatement? = null
    private var typeStatement : TypeDefinitionStatement? = null
    private var right: DefaultExpression? = null
    private val exceptions = CompilationExceptionsCollection()

    override val type: SyntaxType
        get() = SyntaxType.DECLARATION

    override fun getComponents(): List<AbstractStatement> {
        val x = mutableListOf(left ?: return emptyList())
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

            if(transmitter.expectMaybe(tokenOf(MUTABLE_ID)).isPresent)
                name = MUTABLE_ID

            this.left = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            if(transmitter.expectMaybe(tokenOf(TYPEDEF_ID)).isPresent) {
                typeStatement = TypeDefinitionStatement(context)
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