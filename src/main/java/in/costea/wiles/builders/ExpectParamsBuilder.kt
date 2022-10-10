package `in`.costea.wiles.builders

import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.statics.Constants.TOKENS_INVERSE
import java.util.function.Predicate

class ExpectParamsBuilder private constructor(var foundTest: Predicate<String>) {
    var errorMessage = "Shouldn't happen"
        private set
    var whenRemove: WhenRemoveToken = WhenRemoveToken.Default
        private set
    var isIgnoringNewLine = true
        private set

    var secondaryTest : Predicate<String>? = null

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
        if(secondaryTest == null)
            foundTest = foundTest.or(otherTest)
        else secondaryTest = secondaryTest!!.or(otherTest)
        return this
    }

    fun thenOf(otherTest: Predicate<String>): ExpectParamsBuilder {
        secondaryTest = otherTest
        return this
    }

    companion object {
        @JvmField
        val ANYTHING = Predicate { _: String -> true }

        @JvmStatic
        fun isContainedIn(set: Collection<String?>): Predicate<String> {
            return Predicate { o: String? -> set.contains(o) }
        }

        @JvmStatic
        fun tokenOf(expectedToken: String?): ExpectParamsBuilder {
            return ExpectParamsBuilder { x: String? -> x == expectedToken }
                    .withErrorMessage("Token \"" + TOKENS_INVERSE[expectedToken] + "\" expected!")
        }

        @JvmStatic
        fun tokenOf(found: Predicate<String>): ExpectParamsBuilder {
            return ExpectParamsBuilder(found)
        }
    }
}