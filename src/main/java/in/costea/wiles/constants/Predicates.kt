package `in`.costea.wiles.constants

import `in`.costea.wiles.builders.ExpectParamsBuilder
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import java.util.function.Predicate

object Predicates {
    @JvmField
    val IS_IDENTIFIER = Predicate { x: String -> x.length > 1 && x.startsWith(Tokens.IDENTIFIER_START) }
    private val IS_TEXT_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(Tokens.STRING_START) }
    private val IS_NUMBER_LITERAL = Predicate { x: String -> x.length > 1 && x.startsWith(Tokens.NUM_START) }
    private val IS_KEYWORD_LITERAL= Predicate { x:String -> Tokens.KEYWORD_LITERALS.contains(x) }
    @JvmField
    val IS_LITERAL: Predicate<String> = IS_IDENTIFIER.or(IS_TEXT_LITERAL).or(IS_NUMBER_LITERAL).or(IS_KEYWORD_LITERAL)

    @JvmField
    val EXPECT_TERMINATOR = tokenOf(ExpectParamsBuilder.isContainedIn(Tokens.TERMINATORS))
        .dontIgnoreNewLine().withErrorMessage("End of line or semicolon expected!")
}