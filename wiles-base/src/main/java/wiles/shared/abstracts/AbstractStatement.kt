package wiles.shared.abstracts

import wiles.parser.builders.ParserContext
import wiles.shared.data.TokenLocation
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.constants.Utils
import wiles.shared.enums.SyntaxType

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