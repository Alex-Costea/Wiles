package `in`.costea.wiles.commands.expressions

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.TokenCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.services.TokenTransmitter

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
        throw IllegalStateException("Cannot be processed!")
    }
}