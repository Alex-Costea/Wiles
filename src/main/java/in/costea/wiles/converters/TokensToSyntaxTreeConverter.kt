package `in`.costea.wiles.converters

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.commands.CodeBlockCommand
import `in`.costea.wiles.commands.MethodCommand
import `in`.costea.wiles.commands.ProgramCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.services.TokenTransmitter
import `in`.costea.wiles.statics.Constants.METHOD_ID
import `in`.costea.wiles.statics.Constants.MAIN_METHOD_NAME

class TokensToSyntaxTreeConverter(tokens: List<Token>) {
    val exceptions: CompilationExceptionsCollection
    private val bodyOnlyMode: Boolean
    private val tokenTransmitter: TokenTransmitter

    init {
        tokenTransmitter = TokenTransmitter(tokens)
        exceptions = CompilationExceptionsCollection()
        val bodyOnlyMode: Boolean = try {
            tokenTransmitter.expectMaybe(tokenOf(METHOD_ID).removeWhen(WhenRemoveToken.Never)).isEmpty
        } catch (e: UnexpectedTokenException) {
            true
        }
        this.bodyOnlyMode = bodyOnlyMode
    }

    fun convert(): ProgramCommand {
        return if (bodyOnlyMode) {
            val programCommand = ProgramCommand(tokenTransmitter)
            val methodCommand = MethodCommand(tokenTransmitter)
            methodCommand.setMethodName(MAIN_METHOD_NAME)
            val methodBodyCommand = CodeBlockCommand(tokenTransmitter, true)
            methodCommand.setMethodBody(methodBodyCommand)
            programCommand.addMethod(methodCommand)
            exceptions.addAll(methodBodyCommand.process())
            programCommand
        } else {
            val syntaxTree = ProgramCommand(tokenTransmitter)
            exceptions.addAll(syntaxTree.process())
            syntaxTree
        }
    }
}