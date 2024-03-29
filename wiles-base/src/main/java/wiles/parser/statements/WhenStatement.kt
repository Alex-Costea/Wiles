package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens

class WhenStatement(context: ParserContext) : AbstractStatement(context) {
    private val expression = DefaultExpression(context)
    private val branches : MutableList<Pair<AbstractStatement,AbstractStatement>> = mutableListOf()

    override val syntaxType: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): MutableList<AbstractStatement> {
        val list = mutableListOf<AbstractStatement>(expression)
        list.addAll(branches.flatMap { (x, y) -> listOf(x, y) })
        return list
    }
    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            exceptions.addAll(expression.process())
            val isOnlyOne = transmitter.expectMaybe(tokenOf(Tokens.START_BLOCK_ID)).isEmpty
            while(true)
            {
                val type : AbstractStatement
                val body : AbstractStatement
                if(!isOnlyOne && transmitter.expectMaybe(tokenOf(Tokens.END_BLOCK_ID)).isPresent)
                    break
                val expectMaybeElse = transmitter.expectMaybe(tokenOf(Tokens.ELSE_ID))
                val isDefaultCondition = expectMaybeElse.isPresent
                if(!isDefaultCondition) transmitter.expect(tokenOf(Tokens.IS_ID))
                type = if(!isDefaultCondition) TypeAnnotationStatement(context)
                else TokenStatement(expectMaybeElse.get(),context)
                body = CodeBlockStatement(context)
                exceptions.addAll(type.process())
                exceptions.addAll(body.process())
                branches.add(Pair(type, body))
                if(isOnlyOne || isDefaultCondition) {
                    if(isDefaultCondition) transmitter.expect(tokenOf(Tokens.END_BLOCK_ID))
                    break
                }
            }
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}