package wiles.parser.statements

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.StatementInterface
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Utils

@JsonPropertyOrder("parsed", "name", "type", "location", "components")
abstract class AbstractStatement(val context: Context) : StatementInterface
{
    @JvmField
    protected val transmitter = context.transmitter

    abstract override val type: SyntaxType

    override var name = ""

    override var location: TokenLocation? = null

    abstract fun process(): CompilationExceptionsCollection

    override fun toString(): String {
        return Utils.statementToString(name,type,getComponents())
    }

    abstract override fun getComponents(): MutableList<AbstractStatement>
}