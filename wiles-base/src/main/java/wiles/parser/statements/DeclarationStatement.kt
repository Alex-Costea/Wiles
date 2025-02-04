package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.parser.statements.expressions.DefaultExpression
import wiles.parser.statements.expressions.TypeDefExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.CONST_CANT_BE_VAR_ERROR
import wiles.shared.constants.ErrorMessages.EXPECTED_GLOBAL_VALUE_ERROR
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANNOTATE_ID
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.GLOBAL_ID
import wiles.shared.constants.Tokens.VARIABLE_ID

class DeclarationStatement(
    context: ParserContext,
    private val isParam: Boolean = false
)
    : AbstractStatement(context) {
    private var left: TokenStatement? = null
    private var typeStatement : TypeDefExpression? = null
    private var right: DefaultExpression? = null
    private val exceptions = CompilationExceptionsCollection()

    override val syntaxType: SyntaxType
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
            val expectParams = if(isParam) tokenOf(ANON_ARG_ID).or(CONST_ID)
                else tokenOf(VARIABLE_ID).or(CONST_ID).or(GLOBAL_ID)

            val nameTokens = mutableListOf<Token>()
            while(transmitter.expectMaybe(expectParams.removeWhen(WhenRemoveToken.Never)).isPresent){
                val newValue = transmitter.expect(expectParams.removeWhen(WhenRemoveToken.Always))
                nameTokens.add(newValue)
            }

            val nameStrings = nameTokens.map { it.content }
            val contradictory = listOf(CONST_ID, VARIABLE_ID)
            if(nameStrings.containsAll(contradictory))
            {
                throw UnexpectedTokenException(CONST_CANT_BE_VAR_ERROR,
                    nameTokens.filter { it.content == CONST_ID || it.content == VARIABLE_ID }[0].location)
            }

            name = nameTokens.joinToString("; ") { it.content }

            this.left = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            if(transmitter.expectMaybe(tokenOf(ANNOTATE_ID)).isPresent) {
                typeStatement = TypeDefExpression(context)
                typeStatement!!.process().throwFirstIfExists()
                if(nameStrings.contains(GLOBAL_ID))
                {
                    transmitter.expect(tokenOf(ASSIGN_ID).withErrorMessage(EXPECTED_GLOBAL_VALUE_ERROR))
                    readRight()
                }
                else if(transmitter.expectMaybe(tokenOf(ASSIGN_ID).dontIgnoreNewLine()).isPresent)
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