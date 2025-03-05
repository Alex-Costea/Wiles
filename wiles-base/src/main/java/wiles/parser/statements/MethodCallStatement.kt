package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.InsideMethodCallExpression
import wiles.shared.AbstractStatement
import wiles.shared.SyntaxType
import wiles.shared.WilesException
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID

class MethodCallStatement(context: ParserContext) : AbstractStatement(context) {
    var components: ArrayList<AbstractStatement> = ArrayList()

    override val syntaxType: SyntaxType
        get() = SyntaxType.FUNC_CALL

    override fun process(): WilesExceptionsCollection {
        val exceptions = WilesExceptionsCollection()
        try {
            while (transmitter.expectMaybe(tokenOf(PAREN_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty) {
                val newComp = InsideMethodCallExpression(context)
                exceptions.addAll(newComp.process())
                components.add(newComp)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }
            transmitter.expect(tokenOf(PAREN_END_ID))
        } catch (ex: WilesException) {
            exceptions.add(ex)
        }
        return exceptions
    }

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }
}
