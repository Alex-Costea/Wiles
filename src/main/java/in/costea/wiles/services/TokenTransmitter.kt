package `in`.costea.wiles.services

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.TokenExpectedException
import `in`.costea.wiles.exceptions.UnexpectedEndException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.statics.Constants
import java.util.*

class TokenTransmitter(tokens: List<Token>) {
    private val tokens: LinkedList<Token>
    private var lastLocation: TokenLocation? = null

    init {
        this.tokens = LinkedList(tokens)
        lastLocation = if (tokens.isNotEmpty()) tokens[tokens.size - 1].location() else null
    }

    private fun removeToken() {
        check(!tokensExhausted()) { "Tokens exhausted!" }
        tokens.pop()
    }

    fun tokensExhausted(): Boolean {
        return tokens.isEmpty()
    }

    @Throws(UnexpectedEndException::class, UnexpectedTokenException::class, TokenExpectedException::class)
    fun expect(params: ExpectParamsBuilder): Token {
        val message = params.errorMessage
        var succeeded = false
        if (params.whenRemoveToken == WhenRemoveToken.Default)
            params.removeTokenWhen(WhenRemoveToken.Always)
        return try {
            if (tokensExhausted()) throw UnexpectedEndException(message, lastLocation)
            if (params.isIgnoringNewLine) {
                while (tokens.first.content() == Constants.NEWLINE_ID) {
                    if (tokensExhausted()) throw UnexpectedEndException(message, tokens.first.location())
                    removeToken()
                }
            }
            val token: Token = tokens.first
            val foundTest = params.foundTest
            if (!foundTest.test(token.content()))
                throw TokenExpectedException(message, token.location())
            succeeded = true
            token
        } finally {
            val whenRemoveToken: WhenRemoveToken = params.whenRemoveToken
            assert(whenRemoveToken!=WhenRemoveToken.Default)
            if ((!succeeded && whenRemoveToken == WhenRemoveToken.Always) || (succeeded && whenRemoveToken != WhenRemoveToken.Never))
                if (!tokensExhausted())
                    removeToken()
        }
    }

    @Throws(UnexpectedTokenException::class)
    fun expectMaybe(expectParamsBuilder: ExpectParamsBuilder): Optional<Token> {
        return try {
            if (expectParamsBuilder.whenRemoveToken == WhenRemoveToken.Default) expectParamsBuilder.removeTokenWhen(WhenRemoveToken.WhenFound)
            Optional.of(expect(expectParamsBuilder))
        } catch (ex: TokenExpectedException) {
            Optional.empty()
        } catch (ex: UnexpectedEndException) {
            Optional.empty()
        }
    }
}