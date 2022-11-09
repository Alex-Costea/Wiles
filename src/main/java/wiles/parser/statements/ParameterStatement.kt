package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.parser.constants.ErrorMessages.LITERAL_EXPECTED_ERROR
import wiles.parser.constants.Predicates.IS_IDENTIFIER
import wiles.parser.constants.Predicates.IS_LITERAL
import wiles.parser.constants.Tokens.ANON_ARG_ID
import wiles.parser.constants.Tokens.ASSIGN_ID
import wiles.parser.constants.Tokens.TYPEDEF_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.exceptions.AbstractCompilationException

class ParameterStatement(context: Context) : AbstractStatement(context) {
    private var nameToken: TokenStatement? = null
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var typeDefinition: TypeDefinitionStatement
    private var defaultValue: TokenStatement? = null

    init {
        typeDefinition = TypeDefinitionStatement(context)
    }

    override val type: SyntaxType
        get() = SyntaxType.PARAMETER

    override fun getComponents(): List<AbstractStatement> {
        val l = mutableListOf(nameToken!!, typeDefinition)
        defaultValue?.let { l.add(it) }
        return l.toList()
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if(transmitter.expectMaybe(tokenOf(ANON_ARG_ID)).isPresent)
                name = ANON_ARG_ID
            nameToken = TokenStatement(
                transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)
            transmitter.expect(tokenOf(TYPEDEF_ID))
            exceptions.addAll(typeDefinition.process())
            if (transmitter.expectMaybe(tokenOf(ASSIGN_ID)).isPresent) {
                defaultValue = TokenStatement(
                    transmitter.expect(tokenOf(IS_LITERAL).withErrorMessage(LITERAL_EXPECTED_ERROR)),context)
            }
        } catch (e: AbstractCompilationException) {
            exceptions.add(e)
        }
        return exceptions
    }
}