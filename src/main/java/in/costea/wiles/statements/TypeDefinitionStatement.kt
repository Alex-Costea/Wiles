package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.builders.IsWithin
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.constants.Types.TYPES
import `in`.costea.wiles.constants.ErrorMessages.TYPE_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates.IS_CONTAINED_IN

class TypeDefinitionStatement(transmitter: TokenTransmitter,within: IsWithin) : AbstractStatement(transmitter,within) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val type: SyntaxType
        get() = SyntaxType.TYPE

    override fun getComponents(): List<AbstractStatement> {
        return ArrayList()
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val (content) = transmitter.expect(tokenOf(IS_CONTAINED_IN(TYPES.keys)).withErrorMessage(TYPE_EXPECTED_ERROR))
            name = TYPES[content]!!
            //TODO: all the other type stuff
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}