package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.CASE_ID
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.IF_ID
import wiles.shared.constants.Tokens.TERMINATOR_ID
import wiles.shared.constants.Tokens.WHEN_ID

class WhenStatement(context: Context) : AbstractStatement(context) {

    private val branches : MutableList<Pair<AbstractStatement,AbstractStatement>> = mutableListOf()

    override val type: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): MutableList<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }.toMutableList()
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        var condition : AbstractStatement
        var body : AbstractStatement
        var isFirst = true
        try
        {
            val ifStatement = transmitter.expectMaybe(tokenOf(IF_ID)).isPresent
            var finalCase = false
            if(!ifStatement)
                transmitter.expect(tokenOf(WHEN_ID))
            while(true)
            {
                if(!isFirst)
                {
                    val tempToken = transmitter.expectMaybe(tokenOf(ELSE_ID))
                    if (tempToken.isPresent) {
                        val handleOtherwise = fun(): Boolean {
                            if (transmitter.expectMaybe(tokenOf(CASE_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                            {
                                return false
                            }
                            condition = TokenStatement(tempToken.get(), context)
                            body = CodeBlockStatement(context)
                            exceptions.addAll(body.process())
                            transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())
                            branches.add(Pair(condition, body))
                            return true
                        }
                        finalCase = !handleOtherwise()
                        if(!finalCase)
                            break
                    }
                }

                if(!ifStatement) {
                    if (!isFirst)
                        transmitter.expect(tokenOf(CASE_ID))
                    else transmitter.expectMaybe(tokenOf(CASE_ID))
                }

                condition = DefaultExpression(context)
                body = CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                exceptions.addAll(body.process())
                branches.add(Pair(condition, body))
                transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())

                if((isFirst && ifStatement) || finalCase)
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