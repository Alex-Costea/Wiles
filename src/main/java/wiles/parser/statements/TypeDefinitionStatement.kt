package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ASSIGN_ID

class TypeDefinitionStatement(context: Context) : AbstractStatement(context) {

    private lateinit var tokenStatement: TokenStatement
    private lateinit var typeStatement : TypeAnnotationStatement
    private val exceptions = CompilationExceptionsCollection()

    override val syntaxType: SyntaxType
        get() = SyntaxType.TYPEDEF

    override fun process(): CompilationExceptionsCollection {
        tokenStatement = TokenStatement(transmitter.expect(tokenOf(IS_IDENTIFIER)
            .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)
        transmitter.expect(tokenOf(ASSIGN_ID))
        typeStatement = TypeAnnotationStatement(context)
        exceptions.addAll(typeStatement.process())
        return exceptions
    }

    override fun getComponents(): MutableList<AbstractStatement> {
        return mutableListOf(tokenStatement, typeStatement)
    }

}