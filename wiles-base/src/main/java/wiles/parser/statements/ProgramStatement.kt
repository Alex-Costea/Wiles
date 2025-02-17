package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.shared.WilesException
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.Predicates.EXPECT_TERMINATOR

class ProgramStatement(context: ParserContext) : CodeBlockStatement(context) {
    override fun process(): WilesExceptionsCollection {
        try {
            while (!transmitter.tokensExhausted()) {
                if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) continue
                readOneStatement()
            }
        } catch (ex: WilesException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}