package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.shared.constants.ErrorMessages.NOT_ENOUGH_TYPES_EXCEPTION
import wiles.shared.constants.ErrorMessages.TYPE_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_CONTAINED_IN
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.MAX_NR_TYPES
import wiles.shared.constants.Types.MIN_NR_TYPES
import wiles.shared.constants.Types.REQUIRES_SUBTYPE
import wiles.shared.constants.Types.TYPES
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.shared.AbstractCompilationException
import wiles.parser.exceptions.TokenExpectedException

class TypeDefinitionStatement(context: Context) : AbstractStatement(context) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private val subtypes : ArrayList<AbstractStatement> = ArrayList()
    override val type: SyntaxType
        get() = SyntaxType.TYPE

    override fun getComponents(): List<AbstractStatement> {
        return subtypes
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val (content,location) = transmitter.expect(tokenOf(IS_CONTAINED_IN(TYPES.keys)).withErrorMessage(TYPE_EXPECTED_ERROR))
            name = TYPES[content]!!
            if(REQUIRES_SUBTYPE.contains(name))
            {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val max : Int? = MAX_NR_TYPES[name]
                for(i in 1..(max?:Int.MAX_VALUE)) {
                    if(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    val subType = TypeDefinitionStatement(context)
                    subType.process().throwFirstIfExists()
                    subtypes.add(subType)
                    if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                }
                if(subtypes.size < (MIN_NR_TYPES[name] ?: Int.MAX_VALUE))
                    throw TokenExpectedException(NOT_ENOUGH_TYPES_EXCEPTION,location)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(name == METHOD_ID) {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val funStatement = MethodStatement(context, true)
                funStatement.process().throwFirstIfExists()
                subtypes.add(funStatement)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(transmitter.expectMaybe(tokenOf(MAYBE_ID).dontIgnoreNewLine()).isPresent)
            {
                val oldName = name
                name = EITHER_ID

                val component1 = TypeDefinitionStatement(context)
                component1.name = oldName
                component1.subtypes.addAll(subtypes)
                subtypes.clear()
                subtypes.add(component1)

                val component2 = TypeDefinitionStatement(context)
                component2.name = NOTHING_ID
                subtypes.add(component2)
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}