package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.statements.expressions.DefaultExpression

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