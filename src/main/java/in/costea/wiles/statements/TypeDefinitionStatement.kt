package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.ErrorMessages.TYPE_EXPECTED_ERROR
import `in`.costea.wiles.constants.Predicates.IS_CONTAINED_IN
import `in`.costea.wiles.constants.Tokens.BRACKET_END_ID
import `in`.costea.wiles.constants.Tokens.BRACKET_START_ID
import `in`.costea.wiles.constants.Tokens.MAYBE_ID
import `in`.costea.wiles.constants.Tokens.METHOD_ID
import `in`.costea.wiles.constants.Types.GENERIC_ID
import `in`.costea.wiles.constants.Types.REQUIRES_SUBTYPE
import `in`.costea.wiles.constants.Types.TYPES
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException

class TypeDefinitionStatement(context: Context) : AbstractStatement(context) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private val subtypes : ArrayList<TypeDefinitionStatement> = ArrayList()
    override val type: SyntaxType
        get() = SyntaxType.TYPE

    override fun getComponents(): List<AbstractStatement> {
        return subtypes
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val (content) = transmitter.expect(tokenOf(IS_CONTAINED_IN(TYPES.keys)).withErrorMessage(TYPE_EXPECTED_ERROR))
            name = TYPES[content]!!
            if(REQUIRES_SUBTYPE.contains(name))
            {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val subType = TypeDefinitionStatement(context)
                subType.process().throwFirstIfExists()
                subtypes.add(subType)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(name == METHOD_ID)
                TODO("Function types")
            if(name == GENERIC_ID)
                TODO("Generic types")
            if(transmitter.expectMaybe(tokenOf(MAYBE_ID)).isPresent)
                TODO("Nullable types")
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}