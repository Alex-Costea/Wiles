package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.StatementInterface
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Utils

abstract class AbstractStatement(val context: Context) : StatementInterface
{
    @JvmField
    protected val transmitter = context.transmitter

    abstract override val syntaxType: SyntaxType

    override var name = ""

    override var location: TokenLocation? = null
    override var parsed: Boolean? = null

    abstract fun process(): CompilationExceptionsCollection

    override fun toString(): String {
        return Utils.statementToString(name,syntaxType,getComponents())
    }

    abstract override fun getComponents(): MutableList<AbstractStatement>
}