package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.shared.constants.Tokens.CASE_ID
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.IF_ID
import wiles.shared.constants.Tokens.TERMINATOR_ID
import wiles.shared.constants.Tokens.THEN_ID
import wiles.shared.constants.Tokens.WHEN_ID
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.AbstractCompilationException
import wiles.parser.statements.expressions.ConditionExpression
import wiles.parser.statements.expressions.InnerDefaultExpression

class WhenStatement(context: Context, private val isExpression : Boolean = false) : AbstractStatement(context) {

    private val branches : MutableList<Pair<AbstractStatement,AbstractStatement>> = mutableListOf()

    override val type: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): List<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        var condition : AbstractStatement
        var body : AbstractStatement
        var isFirst = true
        try
        {
            val ifStatement = if(!isExpression) transmitter.expectMaybe(tokenOf(IF_ID)).isPresent else false
            if(!ifStatement)
                transmitter.expect(tokenOf(WHEN_ID))
            while(true)
            {
                if(!isFirst)
                {
                    val tempToken = transmitter.expectMaybe(tokenOf(ELSE_ID))
                    if (tempToken.isPresent) {
                        condition = TokenStatement(tempToken.get(), context)
                        body = if(isExpression) InnerDefaultExpression(context) else CodeBlockStatement(context)
                        exceptions.addAll(body.process())
                        transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())
                        branches.add(Pair(condition, body))
                        break
                    }
                }
                if(!ifStatement) {
                    if (!isFirst)
                        transmitter.expect(tokenOf(CASE_ID))
                    else transmitter.expectMaybe(tokenOf(CASE_ID))
                }

                condition = ConditionExpression(context)
                body = if(isExpression) InnerDefaultExpression(context) else CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                if(isExpression)
                    transmitter.expect(tokenOf(THEN_ID))
                else transmitter.expectMaybe(tokenOf(THEN_ID))
                exceptions.addAll(body.process())
                branches.add(Pair(condition, body))
                transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())

                if(isFirst && ifStatement)
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