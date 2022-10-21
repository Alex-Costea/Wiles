package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.isContainedIn
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.TYPES

class TypeDefinitionCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val type: SyntaxType
        get() = SyntaxType.TYPE

    override fun getComponents(): List<AbstractCommand> {
        return ArrayList()
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val (content) = transmitter.expect(tokenOf(isContainedIn(TYPES.keys)).withErrorMessage("Type expected!"))
            name = TYPES[content]!!
            //TODO: all the other type stuff
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}