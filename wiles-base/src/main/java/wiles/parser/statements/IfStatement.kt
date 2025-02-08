package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.DEFAULT_ID
import wiles.shared.constants.Tokens.END_BLOCK_ID
import wiles.shared.constants.Tokens.IF_ID

class IfStatement(context: ParserContext) : AbstractStatement(context) {

    private val branches : MutableList<Pair<AbstractStatement,AbstractStatement>> = mutableListOf()

    override val syntaxType: SyntaxType
        get() = SyntaxType.IF

    override fun getComponents(): MutableList<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }.toMutableList()
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            transmitter.expect(tokenOf(IF_ID))
            val isOnlyOne = transmitter.expectMaybe(tokenOf(Tokens.START_BLOCK_ID)).isEmpty
            while(true)
            {
                val condition : AbstractStatement
                val body : AbstractStatement
                if(!isOnlyOne && transmitter.expectMaybe(tokenOf(END_BLOCK_ID)).isPresent)
                    break
                val expectMaybeElse = transmitter.expectMaybe(tokenOf(DEFAULT_ID))
                val isDefaultCondition = expectMaybeElse.isPresent
                condition = if(!isDefaultCondition) DefaultExpression(context)
                            else TokenStatement(expectMaybeElse.get(),context)
                body = CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                exceptions.addAll(body.process())
                branches.add(Pair(condition, body))
                if(isOnlyOne || isDefaultCondition) {
                    if(isDefaultCondition) transmitter.expect(tokenOf(END_BLOCK_ID))
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