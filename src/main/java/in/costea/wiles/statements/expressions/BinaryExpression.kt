package `in`.costea.wiles.statements.expressions

import `in`.costea.wiles.statements.AbstractStatement
import `in`.costea.wiles.statements.TokenStatement
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.ErrorMessages.CANNOT_BE_PROCESSED_ERROR

class BinaryExpression(
    transmitter: TokenTransmitter,
    operation: TokenStatement?,
    left: AbstractStatement?,
    right: AbstractStatement
) : AbstractExpression(transmitter) {
    init {
        this.left=left
        this.operation=operation
        this.right=right
    }

    override fun process(): CompilationExceptionsCollection {
        throw IllegalStateException(CANNOT_BE_PROCESSED_ERROR)
    }
}