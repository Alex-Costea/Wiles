package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.PAREN_END_ID
import wiles.shared.constants.Tokens.PAREN_START_ID

class TypeLiteralStatement (context: ParserContext)
    : AbstractStatement(context) {
    private val typeStatement = TypeStatement(context)
    private var components = mutableListOf<AbstractStatement>()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val syntaxType: SyntaxType
        get() = SyntaxType.TYPE_LITERAL

    override fun process(): CompilationExceptionsCollection {
        transmitter.expect(tokenOf(PAREN_START_ID))
        try{
            typeStatement.process()
        }
        catch (e : AbstractCompilationException)
        {
            exceptions.add(e)
        }
        transmitter.expect(tokenOf(PAREN_END_ID))
        name = typeStatement.name
        components = typeStatement.getComponents()
        return exceptions
    }

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }
}