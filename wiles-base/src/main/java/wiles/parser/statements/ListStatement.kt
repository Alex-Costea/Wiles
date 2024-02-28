package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.InnerDefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID

class ListStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.LIST
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val newComp = InnerDefaultExpression(context)
                newComp.process().throwFirstIfExists()
                components.add(newComp)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(tokenOf(BRACKET_END_ID)).location
            if(transmitter.expectMaybe(tokenOf(Tokens.TYPE_ANNOTATION_ID).dontIgnoreNewLine()).isPresent) {
                val typeStatement = TypeAnnotationStatement(context)
                typeStatement.process().throwFirstIfExists()
                components.add(0,typeStatement)
            }
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}