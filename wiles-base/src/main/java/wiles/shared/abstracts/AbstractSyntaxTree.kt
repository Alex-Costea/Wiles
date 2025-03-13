package wiles.shared.abstracts

import wiles.shared.constants.Utils
import wiles.shared.data.TokenLocation
import wiles.shared.enums.SyntaxType

@Suppress("DuplicatedCode")
class AbstractSyntaxTree(
    private val components : List<AbstractSyntaxTree>,
    override val location: TokenLocation?,
    override val syntaxType: SyntaxType,
    val details : List<String>,
    ) : SharedStatementInterface {

    override fun getStatementName(): String {
        return details.joinToString("; ")
    }

    override fun getComponents(): List<AbstractSyntaxTree> {
        return components
    }

    override fun toString(): String {
        return Utils.statementToString(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractSyntaxTree

        if (components != other.components) return false
        if (location != other.location) return false
        if (syntaxType != other.syntaxType) return false
        if (details != other.details) return false

        return true
    }

    override fun hashCode(): Int {
        var result = components.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + syntaxType.hashCode()
        result = 31 * result + details.hashCode()
        return result
    }


}
