package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.InnerDefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.BRACE_END_ID
import wiles.shared.constants.Tokens.BRACE_START_ID
import wiles.shared.constants.Tokens.DATA_ID
import wiles.shared.constants.Tokens.RIGHT_ARROW_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.STRING_START
import wiles.shared.constants.Tokens.TYPE_ANNOTATION_ID
import wiles.shared.constants.Types.DICT_ID

class DictStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.DICT
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    private fun convertIdentifierToString(tokenStatement: TokenStatement) : InnerDefaultExpression{
        val name = tokenStatement.token.content
        val expression = InnerDefaultExpression(context)
        val newToken = Token(STRING_START + name.substring(1), tokenStatement.token.location)
        expression.left = TokenStatement(newToken, context)
        return expression
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            val isData = transmitter.expectMaybe(tokenOf(DATA_ID)).isPresent
            if(isData)
                name = DATA_ID
            transmitter.expect(tokenOf(BRACE_START_ID))
            while(transmitter.expectMaybe(tokenOf(BRACE_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                if(!isData)
                {
                    val newComp1 = InnerDefaultExpression(context)
                    newComp1.process().throwFirstIfExists()
                    components.add(newComp1)
                }
                else{
                    val newComp1 = TokenStatement(transmitter.expect(tokenOf(Predicates.IS_IDENTIFIER)
                        .withErrorMessage(ErrorMessages.IDENTIFIER_EXPECTED_ERROR)),context)
                    components.add(convertIdentifierToString(newComp1))
                }

                if(!isData)
                    transmitter.expect(tokenOf(RIGHT_ARROW_ID))
                else transmitter.expect(tokenOf(ASSIGN_ID))

                val newComp2 = InnerDefaultExpression(context)
                newComp2.process().throwFirstIfExists()
                components.add(newComp2)

                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(tokenOf(BRACE_END_ID)).location
            if(!isData) {
                if(transmitter.expectMaybe(tokenOf(TYPE_ANNOTATION_ID).dontIgnoreNewLine()).isPresent) {
                    val typeStatement1 = TypeAnnotationStatement(context)
                    typeStatement1.process().throwFirstIfExists()

                    transmitter.expect(tokenOf(RIGHT_ARROW_ID))

                    val typeStatement2 = TypeAnnotationStatement(context)
                    typeStatement2.process().throwFirstIfExists()

                    val finalTypeStatement = TypeAnnotationStatement(context)
                    finalTypeStatement.name = DICT_ID
                    finalTypeStatement.getComponents().add(typeStatement1)
                    finalTypeStatement.getComponents().add(typeStatement2)
                    components.add(0, finalTypeStatement)
                }
            }
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}