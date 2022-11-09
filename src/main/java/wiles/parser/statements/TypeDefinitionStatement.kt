package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.TYPE_EXPECTED_ERROR
import wiles.parser.constants.Predicates.IS_CONTAINED_IN
import wiles.parser.constants.Tokens
import wiles.parser.constants.Tokens.PAREN_END_ID
import wiles.parser.constants.Tokens.PAREN_START_ID
import wiles.parser.constants.Tokens.MAYBE_ID
import wiles.parser.constants.Tokens.METHOD_ID
import wiles.parser.constants.Types.GENERIC_ID
import wiles.parser.constants.Types.MAX_NR_TYPES
import wiles.parser.constants.Types.REQUIRES_SUBTYPE
import wiles.parser.constants.Types.TYPES
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import kotlin.collections.ArrayList

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
                transmitter.expect(tokenOf(PAREN_START_ID))
                val max : Int? = MAX_NR_TYPES[name]
                for(i in 1..(max?:Int.MAX_VALUE)) {
                    if(transmitter.expectMaybe(tokenOf(PAREN_END_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    val subType = TypeDefinitionStatement(context)
                    subType.process().throwFirstIfExists()
                    subtypes.add(subType)
                    if (transmitter.expectMaybe(tokenOf(Tokens.SEPARATOR_ID)).isEmpty) break
                }
                transmitter.expect(tokenOf(PAREN_END_ID))
            }
            if(name == METHOD_ID)
                TODO("Function types")
            if(name == GENERIC_ID)
                TODO("Generic types")
            if(transmitter.expectMaybe(tokenOf(MAYBE_ID).dontIgnoreNewLine()).isPresent)
                TODO("Nullable types")
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}