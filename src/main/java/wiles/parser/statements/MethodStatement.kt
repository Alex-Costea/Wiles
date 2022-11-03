package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.Predicates.IS_IDENTIFIER
import wiles.parser.constants.Tokens.NOTHING_ID
import wiles.parser.constants.Tokens.RIGHT_ARROW_ID
import wiles.parser.constants.Tokens.PAREN_END_ID
import wiles.parser.constants.Tokens.PAREN_START_ID
import wiles.parser.constants.Tokens.SEPARATOR_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException

class MethodStatement(oldContext : Context) : AbstractStatement(oldContext.setWithinMethod(true)) {
    private val parameters: MutableList<ParameterStatement> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    private var returnType: TypeDefinitionStatement
    private var methodBody: CodeBlockStatement

    init {
        returnType = TypeDefinitionStatement(context)
        methodBody = CodeBlockStatement(context)
        returnType.name = NOTHING_ID
    }

    override val type: SyntaxType
        get() = SyntaxType.METHOD

    override fun getComponents(): List<AbstractStatement> {
        val components = ArrayList<AbstractStatement>()
        components.add(returnType)
        components.addAll(parameters)
        components.add(methodBody)
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            if(transmitter.expectMaybe(tokenOf(PAREN_START_ID)).isPresent)
            {
                //TODO: args param
                while (transmitter.expectMaybe(tokenOf(IS_IDENTIFIER).removeWhen(WhenRemoveToken.Never)).isPresent) {
                    val parameterStatement = ParameterStatement(context)
                    exceptions.addAll(parameterStatement.process())
                    parameters.add(parameterStatement)
                    if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
                }
                transmitter.expect(tokenOf(PAREN_END_ID))
            }

            //Return type
            if (transmitter.expectMaybe(tokenOf(RIGHT_ARROW_ID)).isPresent) {
                returnType = TypeDefinitionStatement(context)
                exceptions.addAll(returnType.process())
            }

            //Read body
            exceptions.addAll(methodBody.process())
        } catch (ex: AbstractCompilationException) {
            exceptions.add(ex)
        }
        return exceptions
    }
}