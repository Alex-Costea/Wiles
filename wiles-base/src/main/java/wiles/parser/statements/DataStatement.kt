package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.DeclarationType
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens

class DataStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.DATA
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.DATA_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val comp = DeclarationStatement(context, DeclarationType.DATA_PARAM)
                comp.process().throwFirstIfExists()
                components.add(comp)

                if (transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(ExpectParamsBuilder.tokenOf(Tokens.DATA_END_ID)).location
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}