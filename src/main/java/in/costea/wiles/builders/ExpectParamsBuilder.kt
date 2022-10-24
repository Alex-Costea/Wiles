package `in`.costea.wiles.builders

import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.constants.Tokens.TOKENS_INVERSE
import `in`.costea.wiles.constants.ErrorMessages.TOKEN_EXPECTED_ERROR
import java.util.function.Predicate

class ExpectParamsBuilder private constructor(var foundTest: Predicate<String>) {
    var errorMessage: String? = null
        private set
    var whenRemove: WhenRemoveToken = WhenRemoveToken.Default
        private set
    var isIgnoringNewLine = true
        private set

    fun withErrorMessage(message: String): ExpectParamsBuilder {
        errorMessage = message
        return this
    }

    fun removeWhen(whenRemove: WhenRemoveToken): ExpectParamsBuilder {
        this.whenRemove = whenRemove
        return this
    }

    fun dontIgnoreNewLine(): ExpectParamsBuilder {
        isIgnoringNewLine = false
        return this
    }

    fun or(otherTest: Predicate<String>): ExpectParamsBuilder {
        foundTest = foundTest.or(otherTest)
        return this
    }

    fun or(otherTest: String): ExpectParamsBuilder {
        or { x -> x == otherTest }
        return this
    }

    companion object {
        @JvmField
        val ANYTHING = Predicate { _: String -> true }

        @JvmStatic
        fun isContainedIn(set: Collection<String>): Predicate<String> {
            return Predicate { o: String -> set.contains(o) }
        }

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