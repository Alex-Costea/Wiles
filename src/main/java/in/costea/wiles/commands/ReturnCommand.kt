package `in`.costea.wiles.commands

import `in`.costea.wiles.commands.expressions.RightSideExpressionCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter

class ReturnCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    override val type: SyntaxType
        get() = SyntaxType.RETURN

    private val expression = RightSideExpressionCommand(transmitter)

    override fun getComponents(): List<AbstractCommand> {
        return listOf(expression)
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try {
            exceptions.addAll(expression.process())
        }
        catch(ex : AbstractCompilationException)
        {
            exceptions.add(ex)
        }
        return exceptions
    }
}