package in.costea.wiles.statements.expressions;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.statements.AbstractStatement;
import in.costea.wiles.statements.TokenStatement;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.ExpectNext;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.PrecedenceProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.constants.Predicates.EXPECT_TERMINATOR;
import static in.costea.wiles.constants.Predicates.IS_LITERAL;
import static in.costea.wiles.constants.Tokens.*;
import static in.costea.wiles.constants.ErrorMessages.*;

public abstract class AbstractExpression extends AbstractStatement {
    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(STARTING_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
                    .withErrorMessage(EXPRESSION_EXPECTED_ERROR).removeWhen(WhenRemoveToken.Never);
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    protected AbstractStatement left = null;
    protected TokenStatement operation = null;
    protected AbstractStatement right = null;

    protected AbstractExpression(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public final @NotNull SyntaxType getType() {
        return SyntaxType.EXPRESSION;
    }

    @Override
    public final @NotNull List<AbstractStatement> getComponents() {
        var components = new ArrayList<AbstractStatement>();
        if (left != null) components.add(left);
        if (operation != null) components.add(operation);
        else
            assert left == null;
        if(right != null)
            components.add(right);
        return components;
    }

    protected Optional<AbstractStatement> handleSpecialStatements() {
        return Optional.empty();
    }

    protected boolean handleToken(@NotNull Token token) throws AbstractCompilationException {
        if (token.getContent().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedTokenException(NON_MATCHING_BRACKETS_ERROR, token.getLocation());
        return false;
    }

    protected void setComponents(@NotNull PrecedenceProcessor precedenceProcessor)
    {
        @NotNull final AbstractStatement result = precedenceProcessor.getResult();
        if (result instanceof final AbstractExpression expression) {
            this.left = expression.left;
            this.operation = expression.operation;
            this.right = expression.right;
            //Right cannot be null. If left is null, operation must also be null
            assert operation != null || left == null;
            assert right != null;
            return;
        }
        //Is not flattenable
        this.right = result;
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainCurrentToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull var precedenceProcessor = new PrecedenceProcessor(transmitter);
            @NotNull Optional<Token> maybeTempToken;
            @NotNull String content = mainCurrentToken.getContent();

            //Decide what token to expect first
            @NotNull ExpectNext expectNext;
            if (IS_LITERAL.test(content) || ROUND_BRACKETS.contains(content) || STARTING_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else
                expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted()) {
                //Stop parsing expression if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR)) {
                    maybeTempToken = transmitter.expectMaybe(EXPECT_TERMINATOR);
                    if (maybeTempToken.isPresent())
                        if (handleToken(maybeTempToken.get()))
                            break;
                }

                //Handle end and assign tokens
                maybeTempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).or(ASSIGN_ID)
                        .removeWhen(WhenRemoveToken.Never));
                if (maybeTempToken.isPresent())
                    if (handleToken(maybeTempToken.get()))
                        break;

                //Handle method calls and inner expressions
                if (transmitter.expectMaybe(tokenOf(ROUND_BRACKET_START_ID)).isPresent()) {
                    if (expectNext == ExpectNext.OPERATOR) //Method call
                        throw new RuntimeException(NOT_YET_IMPLEMENTED_ERROR);
                    else { //Inner expressions
                        var newExpression = new InnerExpression(this.transmitter);
                        newExpression.process().throwFirstIfExists();
                        precedenceProcessor.add(newExpression);
                        expectNext = ExpectNext.OPERATOR;
                        continue;
                    }
                }

                //Handle closing brackets token
                maybeTempToken = transmitter.expectMaybe(tokenOf(ROUND_BRACKET_END_ID));
                if (maybeTempToken.isPresent()) {
                    mainCurrentToken = maybeTempToken.get();
                    if (handleToken(mainCurrentToken))
                        break;
                }

                //Special statements
                if (expectNext == ExpectNext.TOKEN) {
                    Optional<AbstractStatement> maybeStatement;
                    if ((maybeStatement = handleSpecialStatements()).isPresent()) {
                        AbstractStatement statement = maybeStatement.get();
                        statement.process().throwFirstIfExists();
                        precedenceProcessor.add(maybeStatement.get());
                        expectNext = ExpectNext.OPERATOR;
                        continue;
                    }
                }

                //Handle unary operators
                if (expectNext == ExpectNext.TOKEN) {
                    maybeTempToken = transmitter.expectMaybe(tokenOf(isContainedIn(STARTING_OPERATORS)));
                    if (maybeTempToken.isPresent()) {
                        mainCurrentToken = maybeTempToken.get();
                        if (INFIX_OPERATORS.contains(mainCurrentToken.getContent()))
                            mainCurrentToken = new Token(UNARY_ID + mainCurrentToken.getContent(),
                                    mainCurrentToken.getLocation());
                        precedenceProcessor.add(new TokenStatement(transmitter, mainCurrentToken));
                        continue;
                    }
                }

                //Expect the next token
                if (expectNext == ExpectNext.OPERATOR)
                    mainCurrentToken = transmitter.expect(tokenOf(isContainedIn(INFIX_OPERATORS))
                            .withErrorMessage(OPERATOR_EXPECTED_ERROR));
                else
                    mainCurrentToken = transmitter.expect(tokenOf(isContainedIn(STARTING_OPERATORS)).or(IS_LITERAL)
                            .withErrorMessage(IDENTIFIER_OR_UNARY_OPERATOR_EXPECTED_ERROR));

                //Add token and change next expected token
                precedenceProcessor.add(new TokenStatement(transmitter, mainCurrentToken));
                expectNext = (expectNext == ExpectNext.OPERATOR) ? ExpectNext.TOKEN : ExpectNext.OPERATOR;
            }

            //Final processing
            if (expectNext == ExpectNext.TOKEN)
                throw new UnexpectedEndException(EXPRESSION_UNFINISHED_ERROR, mainCurrentToken.getLocation());
            if (this instanceof InnerExpression && !mainCurrentToken.getContent().equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedEndException(NON_MATCHING_BRACKETS_ERROR, mainCurrentToken.getLocation());
            setComponents(precedenceProcessor);
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
