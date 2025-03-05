package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.UnexpectedTokenException
import wiles.parser.statements.expressions.DefaultExpression
import wiles.parser.statements.expressions.TypeDefExpression
import wiles.shared.abstracts.AbstractStatement
import wiles.shared.constants.ErrorMessages.CANT_BE_VAR_ERROR
import wiles.shared.constants.ErrorMessages.EXPECTED_INITIALIZATION_ERROR
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Predicates.NOTHING
import wiles.shared.constants.Tokens.ANNOTATE_ID
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.Tokens.DEFAULT_ID
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.constants.Tokens.VARIABLE_ID
import wiles.shared.data.Token
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.enums.DeclarationType
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.WilesException

class DeclarationStatement(
    context: ParserContext,
    private val isParam: DeclarationType
)
    : AbstractStatement(context) {
    private var left: TokenStatement? = null
    private var typeStatement : TypeDefExpression? = null
    private var right: DefaultExpression? = null
    private val exceptions = WilesExceptionsCollection()

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

    override fun process(): WilesExceptionsCollection {
        try {
            transmitter.expectMaybe(tokenOf(DECLARE_ID))
            val expectParams = when(isParam){
                DeclarationType.FUNC_PARAM -> tokenOf(ANON_ARG_ID).or(CONST_ID)
                DeclarationType.TOP_LEVEL -> tokenOf(VARIABLE_ID).or(CONST_ID).or(LEVEL_SCOPE_ID)
                DeclarationType.DATA_PARAM -> tokenOf(NOTHING)
            }

            val nameTokens = mutableListOf<Token>()
            while(transmitter.expectMaybe(expectParams.removeWhen(WhenRemoveToken.Never)).isPresent){
                val newValue = transmitter.expect(expectParams.removeWhen(WhenRemoveToken.Always))
                nameTokens.add(newValue)
            }

            val nameStrings = nameTokens.map { it.content }
            if(nameStrings.contains(VARIABLE_ID) && (nameStrings.any { CANT_BE_VAR.contains(it) }))
            {
                throw UnexpectedTokenException(CANT_BE_VAR_ERROR,
                    nameTokens.filter { it.content == VARIABLE_ID }[0].location)
            }

            name = nameTokens.joinToString("; ") { it.content }

            this.left = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            if(transmitter.expectMaybe(tokenOf(ANNOTATE_ID)).isPresent) {
                typeStatement = TypeDefExpression(context)
                typeStatement!!.process().throwFirstIfExists()
                if(nameStrings.any{ it == LEVEL_SCOPE_ID || it == DEFAULT_ID})
                {
                    transmitter.expect(tokenOf(ASSIGN_ID).withErrorMessage(EXPECTED_INITIALIZATION_ERROR))
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
        } catch (ex: WilesException) {
            exceptions.add(ex)
        }
        return exceptions
    }

    companion object {
        val CANT_BE_VAR = listOf(CONST_ID, LEVEL_SCOPE_ID)
    }
}