package `in`.costea.wiles.constants

import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import `in`.costea.wiles.constants.ErrorMessages.INTERNAL_ERROR
import `in`.costea.wiles.constants.Tokens.ROUND_BRACKET_START_ID
import `in`.costea.wiles.constants.Tokens.STARTING_OPERATORS
import `in`.costea.wiles.constants.Tokens.TERMINATORS
import `in`.costea.wiles.enums.WhenRemoveToken
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
    val IS_CONTAINED_IN = {set: Collection<String> -> Predicate { o: String -> set.contains(o) }}

    @JvmField
    val EXPECT_TERMINATOR = tokenOf(IS_CONTAINED_IN(TERMINATORS)).dontIgnoreNewLine()
        .withErrorMessage(ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR).removeWhen(WhenRemoveToken.WhenFound).freeze()

    @JvmField
    val ANYTHING = Predicate { _: String -> true }

    @JvmField
    val READ_REST_OF_LINE =tokenOf(IS_CONTAINED_IN(TERMINATORS).negate()).dontIgnoreNewLine()
        .withErrorMessage(INTERNAL_ERROR).removeWhen(WhenRemoveToken.WhenFound).freeze()

    @JvmField
    val START_OF_EXPRESSION =tokenOf(IS_CONTAINED_IN(STARTING_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
            .withErrorMessage(EXPRESSION_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never).freeze()
}