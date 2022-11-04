package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.Tokens.BRACKET_END_ID
import wiles.parser.constants.Tokens.SEPARATOR_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.statements.expressions.InsideListLiteralExpression

class ListStatement(context: Context) : AbstractStatement(context) {
    override val type = SyntaxType.LIST
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): List<AbstractStatement> {
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val newComp =
                    InsideListLiteralExpression(context)
                newComp.process().throwFirstIfExists()
                components.add(newComp)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            transmitter.expect(tokenOf(BRACKET_END_ID))
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}