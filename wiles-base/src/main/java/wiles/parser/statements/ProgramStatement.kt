package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.constants.Predicates.EXPECT_TERMINATOR

class ProgramStatement(context: ParserContext) : CodeBlockStatement(context) {
    override fun process(): CompilationExceptionsCollection {
        try {
            while (!transmitter.tokensExhausted()) {
                if (transmitter.expectMaybe(EXPECT_TERMINATOR).isPresent) continue
                readOneStatement()
            }
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}