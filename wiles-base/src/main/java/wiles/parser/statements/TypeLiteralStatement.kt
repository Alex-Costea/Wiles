package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.TokenExpectedException
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.ErrorMessages.NOT_ENOUGH_TYPES_ERROR
import wiles.shared.constants.Predicates.IS_CONTAINED_IN
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.BRACKET_END_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.DATA_ID
import wiles.shared.constants.Tokens.FUNC_TYPE_ID
import wiles.shared.constants.Tokens.MAX_NR_TYPES
import wiles.shared.constants.Tokens.MIN_NR_TYPES
import wiles.shared.constants.Tokens.REQUIRES_SUBTYPE
import wiles.shared.constants.Tokens.SEPARATOR_ID
import wiles.shared.constants.Tokens.TYPES
import wiles.shared.constants.Tokens.TYPE_ID

class TypeLiteralStatement (context: ParserContext)
    : AbstractStatement(context) {
    private val subtypes : ArrayList<AbstractStatement> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    override val syntaxType: SyntaxType
        get() = SyntaxType.TYPE

    override fun process(): CompilationExceptionsCollection {
        transmitter.expectMaybe(tokenOf(TYPE_ID))
        try {
            val tokenMaybe = transmitter.expectMaybe(tokenOf(IS_CONTAINED_IN(TYPES)))
            if(tokenMaybe.isPresent)
            {
                val (content,location) = tokenMaybe.get()
                this.location=location
                name = content
                if(REQUIRES_SUBTYPE.contains(name))
                {
                    transmitter.expect(tokenOf(BRACKET_START_ID))
                    val max : Int? = MAX_NR_TYPES[name]
                    for(i in 1..(max?:Int.MAX_VALUE)) {
                        if(transmitter.expectMaybe(tokenOf(BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
                            break
                        val subType = TypeLiteralStatement(context)
                        subType.process().throwFirstIfExists()
                        subtypes.add(subType)
                        if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                    }
                    if(subtypes.size < (MIN_NR_TYPES[name] ?: Int.MAX_VALUE))
                        throw TokenExpectedException(NOT_ENOUGH_TYPES_ERROR,location)
                    transmitter.expect(tokenOf(BRACKET_END_ID))
                }
                if(name == FUNC_TYPE_ID) {
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
                        transmitter.expect(tokenOf(Tokens.ANNOTATE_ID))
                        val right = TypeLiteralStatement(context)
                        right.process().throwFirstIfExists()
                        classComponents.add(left)
                        classComponents.add(right)
                        if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                    }
                    subtypes.addAll(classComponents)
                    transmitter.expect(tokenOf(BRACKET_END_ID))
                }
            }
            else{
                val expression = DefaultExpression(context)
                expression.process().throwFirstIfExists()
                name = SyntaxType.EXPRESSION.toString()
                this.subtypes.addAll(expression.getComponents())
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }

    override fun getComponents(): MutableList<AbstractStatement> {
        return subtypes
    }
}