package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException

class ReturnStatement(context : Context) : AbstractStatement(context) {
    override val type: SyntaxType
        get() = SyntaxType.RETURN

    private val expression = DefaultExpression(context)

    override fun getComponents(): MutableList<AbstractStatement> {
        return mutableListOf(expression)
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