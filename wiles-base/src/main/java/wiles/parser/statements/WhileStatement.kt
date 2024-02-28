package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.AbstractCompilationException
import wiles.parser.statements.expressions.DefaultExpression

class WhileStatement(oldContext: ParserContext) : AbstractStatement(oldContext.setWithinLoop(true)) {
    private val condition = DefaultExpression(context)
    private val codeBlock = CodeBlockStatement(context)

    override val syntaxType: SyntaxType
        get() = SyntaxType.WHILE

    override fun getComponents(): MutableList<AbstractStatement> {
        return mutableListOf(condition,codeBlock)
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