package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.shared.AbstractCompilationException
import wiles.parser.statements.expressions.InnerDefaultExpression

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
                val newComp = InnerDefaultExpression(context)
                newComp.process().throwFirstIfExists()
                components.add(newComp)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            transmitter.expect(tokenOf(BRACKET_END_ID))
            if(transmitter.expectMaybe(tokenOf(Tokens.TYPEDEF_ID).dontIgnoreNewLine()).isPresent) {
                val typeStatement = TypeDefinitionStatement(context)
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