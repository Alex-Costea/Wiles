package `in`.costea.wiles.builders

import `in`.costea.wiles.constants.ErrorMessages.FROZEN_ERROR
import `in`.costea.wiles.constants.ErrorMessages.ERROR_MESSAGE_EXPECTED_ERROR
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.constants.Tokens.TOKENS_INVERSE
import `in`.costea.wiles.constants.ErrorMessages.TOKEN_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.WHEN_REMOVE_EXPECTED_ERROR
import `in`.costea.wiles.exceptions.InternalErrorException
import java.util.function.Predicate

class ExpectParamsBuilder private constructor(var foundTest: Predicate<String>) {
    var errorMessage: String? = null
        private set
    var whenRemove: WhenRemoveToken = WhenRemoveToken.Default
        private set
    var isIgnoringNewLine = true
        private set
    var frozen = false
    private set
    fun withErrorMessage(message: String): ExpectParamsBuilder {
        checkFrozen()
        errorMessage = message
        return this
    }

    fun removeWhen(whenRemove: WhenRemoveToken): ExpectParamsBuilder {
        checkFrozen()
        this.whenRemove = whenRemove
        return this
    }

    fun dontIgnoreNewLine(): ExpectParamsBuilder {
        checkFrozen()
        isIgnoringNewLine = false
        return this
    }

    fun or(otherTest: Predicate<String>): ExpectParamsBuilder {
        checkFrozen()
        foundTest = foundTest.or(otherTest)
        errorMessage = null
        return this
    }

    fun or(otherTest: String): ExpectParamsBuilder {
        checkFrozen()
        or { x -> x == otherTest }
        errorMessage = null
        return this
    }

    fun freeze() : ExpectParamsBuilder
    {
        frozen = true
        if(errorMessage == null)
            throw InternalErrorException(ERROR_MESSAGE_EXPECTED_ERROR)
        if(whenRemove == WhenRemoveToken.Default)
            throw InternalErrorException(WHEN_REMOVE_EXPECTED_ERROR)
        return this
    }

    private fun checkFrozen()
    {
        if(frozen)
            throw InternalErrorException(FROZEN_ERROR)
    }

    companion object {
        @JvmStatic
        fun tokenOf(expectedToken: String): ExpectParamsBuilder {
            return ExpectParamsBuilder { x: String -> x == expectedToken }
                .withErrorMessage(TOKEN_EXPECTED_ERROR.format(TOKENS_INVERSE[expectedToken]))
        }

        @JvmStatic
        fun tokenOf(found: Predicate<String>): ExpectParamsBuilder {
            return ExpectParamsBuilder(found)
        }
    }
}