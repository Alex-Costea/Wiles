package wiles.parser.services

import wiles.parser.builders.ExpectParamsBuilder
import wiles.parser.constants.ErrorMessages.ERROR_MESSAGE_EXPECTED_ERROR
import wiles.parser.data.Token
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.TokenExpectedException
import wiles.parser.exceptions.UnexpectedEndException
import wiles.parser.constants.Tokens
import wiles.parser.constants.ErrorMessages.INTERNAL_ERROR
import wiles.parser.data.TokenLocation
import java.util.*

class TokenTransmitter(tokens: List<Token>, val lastLocation : TokenLocation) {
    private val tokens = LinkedList(tokens)

    @Throws(UnexpectedEndException::class, TokenExpectedException::class)
    fun expect(params: ExpectParamsBuilder): Token {
        val message = params.errorMessage
        var succeeded = false
        if (!params.frozen && params.whenRemove == WhenRemoveToken.Default)
            params.removeWhen(WhenRemoveToken.Always)
        message?:throw wiles.parser.exceptions.InternalErrorException(ERROR_MESSAGE_EXPECTED_ERROR)
        return try {
            if (tokens.isEmpty()) throw UnexpectedEndException(message, lastLocation)
            if (params.isIgnoringNewLine) {
                while (tokens.first.content == Tokens.NEWLINE_ID) {
                    val token = tokens.pop()
                    if (tokens.isEmpty()) throw UnexpectedEndException(message, token.location)
                }
            }
            val foundTest = params.foundTest
            if (!foundTest.test(tokens.first.content))
                throw TokenExpectedException(message, tokens.first.location)

            succeeded = true
            tokens.first
        } finally {
            val whenRemoveToken: WhenRemoveToken = params.whenRemove
            assert(whenRemoveToken != WhenRemoveToken.Default)
            if ((!succeeded && whenRemoveToken == WhenRemoveToken.Always) || (succeeded && whenRemoveToken != WhenRemoveToken.Never))
                if (!tokens.isEmpty())
                    tokens.pop()
        }
    }

    fun expectMaybe(expectParamsBuilder: ExpectParamsBuilder): Optional<Token> {
        return try {
            if(!expectParamsBuilder.frozen) {
                if (expectParamsBuilder.whenRemove == WhenRemoveToken.Default)
                    expectParamsBuilder.removeWhen(WhenRemoveToken.WhenFound)
                if (expectParamsBuilder.errorMessage == null)
                    expectParamsBuilder.withErrorMessage(INTERNAL_ERROR)
            }
            Optional.of(expect(expectParamsBuilder))
        } catch (ex: TokenExpectedException) {
            Optional.empty()
        } catch (ex: UnexpectedEndException) {
            Optional.empty()
        }
    }

    fun tokensExhausted(): Boolean {
        return tokens.isEmpty()
    }
}