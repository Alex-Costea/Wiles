package wiles.parser.constants

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.EXPRESSION_EXPECTED_ERROR
import wiles.parser.constants.ErrorMessages.INTERNAL_ERROR
import wiles.parser.constants.Tokens.ASSIGN_ID
import wiles.parser.constants.Tokens.BRACKET_END_ID
import wiles.parser.constants.Tokens.BRACKET_START_ID
import wiles.parser.constants.Tokens.DO_ID
import wiles.parser.constants.Tokens.KEYWORD_LITERALS
import wiles.parser.constants.Tokens.METHOD_ID
import wiles.parser.constants.Tokens.NEWLINE_ID
import wiles.parser.constants.Tokens.PAREN_START_ID
import wiles.parser.constants.Tokens.SEPARATOR_ID
import wiles.parser.constants.Tokens.STARTING_OPERATORS
import wiles.parser.constants.Tokens.NEW_STATEMENT_START_KEYWORDS
import wiles.parser.constants.Tokens.START_BLOCK_ID
import wiles.parser.constants.Tokens.TERMINATORS
import wiles.parser.enums.WhenRemoveToken
import java.util.function.Predicate

object Predicates {
    @JvmField
    val IS_IDENTIFIER = Predicate { x: String -> x.startsWith(Tokens.IDENTIFIER_START) }
    private val IS_TEXT_LITERAL = Predicate { x: String -> x.startsWith(Tokens.STRING_START) }
    private val IS_NUMBER_LITERAL = Predicate { x: String -> x.startsWith(Tokens.NUM_START) }
    private val IS_KEYWORD_LITERAL= Predicate { x:String -> KEYWORD_LITERALS.contains(x) }
    @JvmField
    val IS_LITERAL: Predicate<String> = IS_IDENTIFIER.or(IS_TEXT_LITERAL).or(IS_NUMBER_LITERAL).or(IS_KEYWORD_LITERAL)

    @JvmField
    val IS_CONTAINED_IN = {set: Collection<String> -> Predicate { o: String -> set.contains(o) }}

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
    val START_OF_EXPRESSION =tokenOf(IS_CONTAINED_IN(STARTING_OPERATORS)).or(IS_LITERAL)
        .or(PAREN_START_ID).or(BRACKET_START_ID).or(METHOD_ID).or(DO_ID).or(START_BLOCK_ID)
        .withErrorMessage(EXPRESSION_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never).freeze()


    @JvmField
    val START_OF_EXPRESSION_NO_CODE_BLOCK = tokenOf(IS_CONTAINED_IN(STARTING_OPERATORS))
        .or(IS_LITERAL).or(PAREN_START_ID).or(BRACKET_START_ID).or(METHOD_ID)
        .withErrorMessage(EXPRESSION_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never).freeze()

    @JvmField
    val FINALIZE_EXPRESSION = tokenOf(IS_CONTAINED_IN.invoke(TERMINATORS)).or(ASSIGN_ID)
            .or(IS_CONTAINED_IN.invoke(NEW_STATEMENT_START_KEYWORDS)).or(SEPARATOR_ID).or(BRACKET_END_ID)
            .withErrorMessage(INTERNAL_ERROR).dontIgnoreNewLine().removeWhen(WhenRemoveToken.Never).freeze()
}