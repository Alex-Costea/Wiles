package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.Predicates.IS_IDENTIFIER
import `in`.costea.wiles.constants.Tokens.NOTHING_ID
import `in`.costea.wiles.constants.Tokens.RIGHT_ARROW_ID
import `in`.costea.wiles.constants.Tokens.ROUND_BRACKET_END_ID
import `in`.costea.wiles.constants.Tokens.ROUND_BRACKET_START_ID
import `in`.costea.wiles.constants.Tokens.SEPARATOR_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException

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
            transmitter.expect(tokenOf(ROUND_BRACKET_START_ID))

            //TODO: check if arg parameters are t the end
            while (transmitter.expectMaybe(tokenOf(IS_IDENTIFIER).removeWhen(WhenRemoveToken.Never)).isPresent) {
                val parameterStatement = ParameterStatement(context)
                exceptions.addAll(parameterStatement.process())
                parameters.add(parameterStatement)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }

            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID))

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