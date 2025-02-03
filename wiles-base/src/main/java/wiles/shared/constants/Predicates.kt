package wiles.shared.constants

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.enums.WhenRemoveToken
import wiles.shared.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.ErrorMessages.INVALID_EXPRESSION_ERROR
import wiles.shared.constants.Tokens.ANGLE_BRACKET_START_ID
import wiles.shared.constants.Tokens.BRACE_START_ID
import wiles.shared.constants.Tokens.BRACKET_START_ID
import wiles.shared.constants.Tokens.DO_ID
import wiles.shared.constants.Tokens.FUNC_ID
import wiles.shared.constants.Tokens.INFIX_OPERATORS
import wiles.shared.constants.Tokens.KEYWORDS_INDICATING_NEW_EXPRESSION
import wiles.shared.constants.Tokens.NEWLINE_ID
import wiles.shared.constants.Tokens.PAREN_START_ID
import wiles.shared.constants.Tokens.STARTING_OPERATORS
import wiles.shared.constants.Tokens.START_BLOCK_ID
import wiles.shared.constants.Tokens.TERMINATORS
import wiles.shared.constants.Tokens.TYPE_ID
import java.util.function.Predicate

object Predicates {
    @JvmField
    val IS_IDENTIFIER = Predicate { x: String -> x.startsWith(Tokens.IDENTIFIER_START)}
    val IS_TEXT_LITERAL = Predicate { x: String -> x.startsWith(Tokens.STRING_START) }
    val IS_NUMBER_LITERAL = Predicate { x: String -> x.startsWith(Tokens.NUM_START) }
    @JvmField
    val IS_LITERAL: Predicate<String> = IS_IDENTIFIER.or(IS_TEXT_LITERAL).or(IS_NUMBER_LITERAL)

    @JvmField
    val IS_CONTAINED_IN = {set: Collection<String> -> Predicate { o: String -> set.contains(o) }}

    @JvmField
    val STARTS_AS_TOKEN = Predicate { content : String ->
        IS_LITERAL.test(content) || content == PAREN_START_ID || content == Tokens.PAREN_END_ID ||
                content == TYPE_ID || content == BRACKET_START_ID || content == BRACE_START_ID ||
                content == DO_ID || content == ANGLE_BRACKET_START_ID || content == START_BLOCK_ID ||
                STARTING_OPERATORS.contains(content) || content == FUNC_ID }

    @JvmField
    val EXPECT_OPERATOR = tokenOf(IS_CONTAINED_IN.invoke(INFIX_OPERATORS)).withErrorMessage(INVALID_EXPRESSION_ERROR)
        .removeWhen(WhenRemoveToken.Always).freeze()

    @JvmField
    val EXPECT_TOKEN = tokenOf(IS_CONTAINED_IN.invoke(STARTING_OPERATORS)).or(IS_LITERAL)
        .withErrorMessage(INVALID_EXPRESSION_ERROR).removeWhen(WhenRemoveToken.Always).freeze()


    @JvmField
    val EXPECT_TERMINATOR = tokenOf(IS_CONTAINED_IN(TERMINATORS)).dontIgnoreNewLine()
        .withErrorMessage(ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR).removeWhen(WhenRemoveToken.WhenFound).freeze()

    @JvmField
    val EXPECT_TERMINATOR_DONT_REMOVE = tokenOf(IS_CONTAINED_IN(TERMINATORS)).dontIgnoreNewLine()
        .withErrorMessage(ErrorMessages.END_OF_STATEMENT_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never).freeze()


    @JvmField
    val ANYTHING = Predicate { _: String -> true }

    @JvmField
    val READ_REST_OF_LINE =tokenOf { it != NEWLINE_ID }.dontIgnoreNewLine()
        .withErrorMessage(INTERNAL_ERROR).removeWhen(WhenRemoveToken.WhenFound).freeze()

    @JvmField
    val START_OF_EXPRESSION = tokenOf(IS_CONTAINED_IN(STARTING_OPERATORS)).or(IS_LITERAL).or(BRACE_START_ID)
        .or(PAREN_START_ID).or(BRACKET_START_ID).or(FUNC_ID).or(DO_ID).or(START_BLOCK_ID).or(ANGLE_BRACKET_START_ID)
        .or(TYPE_ID).withErrorMessage(EXPRESSION_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never).freeze()

    @JvmField
    val FINALIZE_EXPRESSION = tokenOf(IS_CONTAINED_IN.invoke(KEYWORDS_INDICATING_NEW_EXPRESSION))
            .withErrorMessage(INTERNAL_ERROR).dontIgnoreNewLine().removeWhen(WhenRemoveToken.Never).freeze()
}