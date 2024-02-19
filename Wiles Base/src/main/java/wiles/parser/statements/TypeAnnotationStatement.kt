package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.TokenExpectedException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.ErrorMessages.NOT_ENOUGH_TYPES_ERROR
import wiles.shared.constants.ErrorMessages.TYPE_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_CONTAINED_IN
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.AS_ID
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.DATA_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Types.ALLOWS_GENERICS
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.MAX_NR_TYPES
import wiles.shared.constants.Types.MIN_NR_TYPES
import wiles.shared.constants.Types.REQUIRES_SUBTYPE
import wiles.shared.constants.Types.TYPES

class TypeAnnotationStatement(context: ParserContext, private val allowGenerics : Boolean = false)
    : AbstractStatement(context) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private val subtypes : ArrayList<AbstractStatement> = ArrayList()
    override val syntaxType: SyntaxType
        get() = SyntaxType.TYPE

    override fun getComponents(): MutableList<AbstractStatement> {
        return subtypes
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            val (content,location) = transmitter.expect(tokenOf(IS_CONTAINED_IN(TYPES.keys)
                .or(IS_IDENTIFIER)).withErrorMessage(TYPE_EXPECTED_ERROR))
            this.location=location
            name = TYPES[content]?:content
            if(REQUIRES_SUBTYPE.contains(name))
            {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val max : Int? = MAX_NR_TYPES[name]
                for(i in 1..(max?:Int.MAX_VALUE)) {
                    if(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                        break
                    val subType = TypeAnnotationStatement(context, allowGenerics = ALLOWS_GENERICS.contains(name))
                    subType.process().throwFirstIfExists()
                    subtypes.add(subType)
                    if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                }
                if(subtypes.size < (MIN_NR_TYPES[name] ?: Int.MAX_VALUE))
                    throw TokenExpectedException(NOT_ENOUGH_TYPES_ERROR,location)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(name == METHOD_ID) {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val funStatement = MethodStatement(context, true)
                funStatement.process().throwFirstIfExists()
                subtypes.add(funStatement)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(name == DATA_ID) {
                transmitter.expect(tokenOf(BRACKET_START_ID))
                val classComponents : ArrayList<AbstractStatement> = arrayListOf()
                while(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
                {
                    val left = TokenStatement(transmitter.expect(
                        tokenOf(IS_IDENTIFIER)
                            .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)
                    transmitter.expect(tokenOf(Tokens.TYPE_ANNOTATION_ID))
                    val right = TypeAnnotationStatement(context)
                    right.process().throwFirstIfExists()
                    classComponents.add(left)
                    classComponents.add(right)
                    if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                }
                subtypes.addAll(classComponents)
                transmitter.expect(tokenOf(BRACKET_END_ID))
            }
            if(transmitter.expectMaybe(tokenOf(MAYBE_ID).dontIgnoreNewLine()).isPresent)
            {
                val oldName = name
                name = EITHER_ID

                val component1 = TypeAnnotationStatement(context)
                component1.name = oldName
                component1.location = location
                component1.subtypes.addAll(subtypes)
                subtypes.clear()
                subtypes.add(component1)

                val component2 = TypeAnnotationStatement(context)
                component2.name = NOTHING_ID
                subtypes.add(component2)
            }
            if(transmitter.expectMaybe(tokenOf(Tokens.OR_ID).dontIgnoreNewLine()).isPresent)
            {
                val oldName = name
                name = EITHER_ID

                val component1 = TypeAnnotationStatement(context)
                component1.name = oldName
                component1.location = location
                component1.subtypes.addAll(subtypes)
                subtypes.clear()
                subtypes.add(component1)

                val newStatement = TypeAnnotationStatement(context)
                newStatement.process().throwFirstIfExists()
                subtypes.add(newStatement)
            }
            if(allowGenerics && transmitter.expectMaybe(tokenOf(AS_ID).dontIgnoreNewLine()).isPresent)
            {
                val oldName = name
                name = GENERIC_ID

                val component1 = TypeAnnotationStatement(context)
                component1.name = oldName
                component1.location = location
                component1.subtypes.addAll(subtypes)
                subtypes.clear()
                subtypes.add(component1)

                val identifier = transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage(IDENTIFIER_EXPECTED_ERROR))
                val tokenStatement = TokenStatement(identifier,context)
                subtypes.add(0,tokenStatement)
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}