package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class ReturnStatement(context : Context) : AbstractStatement(context) {
    override val type: SyntaxType
        get() = SyntaxType.RETURN

    private val expression =
        DefaultExpression(context)

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