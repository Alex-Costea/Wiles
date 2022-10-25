package `in`.costea.wiles.statements.expressions

import `in`.costea.wiles.builders.IsWithin
import `in`.costea.wiles.statements.AbstractStatement
import `in`.costea.wiles.statements.TokenStatement
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.ErrorMessages.CANNOT_BE_PROCESSED_ERROR
import `in`.costea.wiles.exceptions.InternalErrorException

class BinaryExpression(
    transmitter: TokenTransmitter,
    operation: TokenStatement?,
    left: AbstractStatement?,
    right: AbstractStatement,
    within: IsWithin
) : AbstractExpression(transmitter, within) {
    init {
        this.left=left
        this.operation=operation
        this.right=right
    }

    override fun process(): CompilationExceptionsCollection {
        throw InternalErrorException(CANNOT_BE_PROCESSED_ERROR)
    }
}