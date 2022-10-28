package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class WhileStatement(oldContext: Context) : AbstractStatement(oldContext.setWithinLoop(true)) {
    private val condition = DefaultExpression(context)
    private val codeBlock = CodeBlockStatement(context)

    override val type: SyntaxType
        get() = SyntaxType.WHILE

    override fun getComponents(): List<AbstractStatement> {
        return listOf(condition,codeBlock)
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            exceptions.addAll(condition.process())
            exceptions.addAll(codeBlock.process())
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}