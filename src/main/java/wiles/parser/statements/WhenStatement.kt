package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.Tokens.CASE_ID
import wiles.parser.constants.Tokens.ELSE_ID
import wiles.parser.constants.Tokens.TERMINATOR_ID
import wiles.parser.constants.Tokens.THEN_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.statements.expressions.ConditionExpression

class WhenStatement(context: Context) : AbstractStatement(context) {

    private val branches : MutableList<Pair<AbstractStatement,CodeBlockStatement>> = mutableListOf()

    override val type: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): List<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        var condition : AbstractStatement
        var blockStatement : CodeBlockStatement
        var isFirst = true
        try
        {
            var startsWithCase = true
            while(true)
            {
                if(!isFirst)
                {
                    val tempToken = transmitter.expectMaybe(tokenOf(ELSE_ID))
                    if (tempToken.isPresent) {
                        condition = TokenStatement(tempToken.get(), context)
                        blockStatement = CodeBlockStatement(context)
                        exceptions.addAll(blockStatement.process())
                        transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())
                        branches.add(Pair(condition, blockStatement))
                        break
                    }
                }

                if(!isFirst)
                    transmitter.expect(tokenOf(CASE_ID))
                else if(transmitter.expectMaybe(tokenOf(CASE_ID)).isEmpty)
                    startsWithCase = false

                condition = ConditionExpression(context)
                blockStatement = CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                transmitter.expectMaybe(tokenOf(THEN_ID))
                exceptions.addAll(blockStatement.process())
                branches.add(Pair(condition, blockStatement))
                transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())

                if(isFirst && !startsWithCase)
                    break
                isFirst = false
            }
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}