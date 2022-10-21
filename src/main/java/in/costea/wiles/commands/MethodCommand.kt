package `in`.costea.wiles.commands

import `in`.costea.wiles.builders.CommandFactory
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.NOTHING_ID
import `in`.costea.wiles.statics.Constants.RIGHT_ARROW_ID
import `in`.costea.wiles.statics.Constants.ROUND_BRACKET_END_ID
import `in`.costea.wiles.statics.Constants.ROUND_BRACKET_START_ID
import `in`.costea.wiles.statics.Constants.SEPARATOR_ID
import java.util.*
import kotlin.collections.ArrayList

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

    override val type: SyntaxType
        get() = SyntaxType.METHOD

    override fun getComponents(): List<AbstractCommand> {
        val components = ArrayList<AbstractCommand>()
        components.add(returnType)
        components.addAll(parameters)
        components.add(methodBody)
        return components
    }

    override fun process(): CompilationExceptionsCollection {
        try {
            //Parameters list
            transmitter.expect(tokenOf(ROUND_BRACKET_START_ID))

            val commandFactory = CommandFactory(transmitter).of(ParameterCommand::class.java)
            var maybeParameterCommand: Optional<AbstractCommand>
            while (commandFactory.createMaybe().also{maybeParameterCommand = it}.isPresent) {
                val parameterCommand = maybeParameterCommand.get() as ParameterCommand
                exceptions.addAll(parameterCommand.process())
                parameters.add(parameterCommand)
                if (transmitter.expectMaybe(tokenOf(SEPARATOR_ID)).isEmpty) break
            }

            transmitter.expect(tokenOf(ROUND_BRACKET_END_ID))

            //Return type
            if (transmitter.expectMaybe(tokenOf(RIGHT_ARROW_ID)).isPresent) {
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