package `in`.costea.wiles.services

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.TokenExpectedException
import `in`.costea.wiles.exceptions.UnexpectedEndException
import `in`.costea.wiles.statics.Constants
import java.util.*

class TokenTransmitter(tokens: List<Token>) {
    private val tokens = LinkedList(tokens)

    private fun removeToken() {
        check(!tokens.isEmpty()) { "Tokens exhausted!" }
        tokens.pop()
    }

    @Throws(UnexpectedEndException::class, TokenExpectedException::class)
    fun expect(params: ExpectParamsBuilder): Token {
        val message = params.errorMessage
        var succeeded = false
        if (params.whenRemove == WhenRemoveToken.Default)
            params.removeWhen(WhenRemoveToken.Always)
        message!!
        return try {
            if (tokens.isEmpty()) throw UnexpectedEndException(message, null)
            if (params.isIgnoringNewLine) {
                while (tokens.first.content == Constants.NEWLINE_ID) {
                    if (tokens.isEmpty()) throw UnexpectedEndException(message, tokens.first.location)
                    removeToken()
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
                    removeToken()
        }
    }

    fun expectMaybe(expectParamsBuilder: ExpectParamsBuilder): Optional<Token> {
        return try {
            if (expectParamsBuilder.whenRemove == WhenRemoveToken.Default)
                expectParamsBuilder.removeWhen(WhenRemoveToken.WhenFound)
            if(expectParamsBuilder.errorMessage==null)
                expectParamsBuilder.withErrorMessage("N/A")
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