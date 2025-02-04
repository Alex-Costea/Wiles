package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.InnerDefaultExpression
import wiles.parser.statements.expressions.TypeDefExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ANNOTATE_ID
import wiles.shared.constants.Tokens.DICT_END_ID
import wiles.shared.constants.Tokens.DICT_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID

class DictStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.DICT
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(tokenOf(DICT_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val newComp1 = InnerDefaultExpression(context)
                newComp1.process().throwFirstIfExists()
                components.add(newComp1)

                transmitter.expect(tokenOf(ANNOTATE_ID))

                val newComp2 = InnerDefaultExpression(context)
                newComp2.process().throwFirstIfExists()
                components.add(newComp2)

                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(tokenOf(DICT_END_ID)).location
            if(transmitter.expectMaybe(tokenOf(ANNOTATE_ID).dontIgnoreNewLine()).isPresent) {
                val typeStatement1 = TypeDefExpression(context)
                typeStatement1.process().throwFirstIfExists()

                transmitter.expect(tokenOf(ANNOTATE_ID))

                val typeStatement2 = TypeDefExpression(context)
                typeStatement2.process().throwFirstIfExists()

                val finalTypeStatement = TypeDefExpression(context)
                finalTypeStatement.name = DICT_ID
                finalTypeStatement.getComponents().add(typeStatement1)
                finalTypeStatement.getComponents().add(typeStatement2)
                components.add(0,finalTypeStatement)
            }
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}