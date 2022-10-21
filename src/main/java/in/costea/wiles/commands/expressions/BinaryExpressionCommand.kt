package `in`.costea.wiles.commands.expressions

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.TokenCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.CANNOT_BE_PROCESSED_ERROR

class BinaryExpressionCommand(
    transmitter: TokenTransmitter,
    operation: TokenCommand?,
    left: AbstractCommand?,
    right: AbstractCommand
) : AbstractExpressionCommand(transmitter) {
    init {
        this.left=left
        this.operation=operation
        this.right=right
    }

    override fun process(): CompilationExceptionsCollection {
        throw IllegalStateException(CANNOT_BE_PROCESSED_ERROR)
    }
}