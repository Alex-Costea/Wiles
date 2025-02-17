package wiles.shared

import wiles.parser.builders.ParserContext
import wiles.shared.constants.Utils

abstract class AbstractStatement(val context: ParserContext) : StatementInterface
{
    @JvmField
    protected val transmitter = context.transmitter

    abstract override val syntaxType: SyntaxType

    override var name = ""

    override var location: TokenLocation? = null

    abstract fun process(): WilesExceptionsCollection

    override fun toString(): String {
        return Utils.statementToString(this)
    }

    abstract override fun getComponents(): MutableList<AbstractStatement>
}