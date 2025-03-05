package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.errors.WilesException
import wiles.shared.abstracts.AbstractStatement
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.enums.SyntaxType

class ReturnStatement(context : ParserContext) : AbstractStatement(context) {
    override val syntaxType: SyntaxType
        get() = SyntaxType.RETURN

    private val expression = DefaultExpression(context)

    override fun getComponents(): MutableList<AbstractStatement> {
        return mutableListOf(expression)
    }

    override fun process(): WilesExceptionsCollection {
        val exceptions = WilesExceptionsCollection()
        try {
            exceptions.addAll(expression.process())
        }
        catch(ex : WilesException)
        {
            exceptions.add(ex)
        }
        return exceptions
    }
}