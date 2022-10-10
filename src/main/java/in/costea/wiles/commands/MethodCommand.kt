package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.COLON_ID
import `in`.costea.wiles.statics.Constants.COMMA_ID
import `in`.costea.wiles.statics.Constants.IS_IDENTIFIER
import `in`.costea.wiles.statics.Constants.NOTHING_ID
import `in`.costea.wiles.statics.Constants.ROUND_BRACKET_END_ID
import `in`.costea.wiles.statics.Constants.ROUND_BRACKET_START_ID
import java.util.*

class MethodCommand(transmitter: TokenTransmitter) : AbstractCommand(transmitter) {
    private val parameters: MutableList<ParameterCommand> = ArrayList()
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()

    private var returnType: TypeDefinitionCommand
    private var methodBody: CodeBlockCommand

    init {
        returnType = TypeDefinitionCommand(transmitter)
        methodBody = CodeBlockCommand(transmitter, false)
        returnType.name = NOTHING_ID
    }

    fun setMethodName(methodName: String) {
        name = methodName
    }

    override val type: SyntaxType
        get() = SyntaxType.METHOD

    fun setMethodBody(methodBody: CodeBlockCommand) {
        this.methodBody = methodBody
    }

    override fun getComponents(): List<AbstractCommand> {
        val components = ArrayList<AbstractCommand>()
        components.add(returnType)
        components.addAll(parameters)
        components.add(methodBody)
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            name = transmitter.expect(tokenOf(IS_IDENTIFIER).withErrorMessage("Expected method name!"))
                .content.substring(1)

            //Parameters list
            transmitter.expect(tokenOf(ROUND_BRACKET_START_ID))
            var maybeToken: Optional<Token>
            while (transmitter.expectMaybe(tokenOf(IS_IDENTIFIER)).also{ maybeToken = it }.isPresent) {
                val parameterCommand = ParameterCommand(transmitter, maybeToken.get())
                exceptions.addAll(parameterCommand.process())
                parameters.add(parameterCommand)
                if (transmitter.expectMaybe(tokenOf(COMMA_ID)).isEmpty) break
            }
            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID))

            //Return type
            if (transmitter.expectMaybe(tokenOf(COLON_ID)).isPresent) {
                returnType = TypeDefinitionCommand(transmitter)
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