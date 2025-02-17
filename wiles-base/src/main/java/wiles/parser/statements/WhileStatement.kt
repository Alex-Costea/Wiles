package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.WilesException
import wiles.shared.AbstractStatement
import wiles.shared.WilesExceptionsCollection
import wiles.shared.SyntaxType

class WhileStatement(oldContext: ParserContext) : AbstractStatement(oldContext) {
    private val condition = DefaultExpression(context)
    private val codeBlock = CodeBlockStatement(context)

    override val syntaxType: SyntaxType
        get() = SyntaxType.WHILE

    override fun getComponents(): MutableList<AbstractStatement> {
        return mutableListOf(condition,codeBlock)
    }

    override fun process(): WilesExceptionsCollection {
        val exceptions = WilesExceptionsCollection()
        try
        {
            exceptions.addAll(condition.process())
            exceptions.addAll(codeBlock.process())
        }
        catch (ex : WilesException){
            exceptions.add(ex)
        }
        return exceptions
    }
}