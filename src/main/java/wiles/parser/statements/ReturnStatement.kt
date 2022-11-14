package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException

class ReturnStatement(context : Context) : AbstractStatement(context) {
    override val type: SyntaxType
        get() = SyntaxType.RETURN

    private val expression =
        wiles.parser.statements.expressions.DefaultExpression(context)

    override fun getComponents(): List<AbstractStatement> {
        return listOf(expression)
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try {
            exceptions.addAll(expression.process())
        }
        catch(ex : AbstractCompilationException)
        {
            exceptions.add(ex)
        }
        return exceptions
    }
}