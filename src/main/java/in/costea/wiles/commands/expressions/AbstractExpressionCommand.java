package in.costea.wiles.commands.expressions;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.commands.AbstractCommand;
import in.costea.wiles.commands.TokenCommand;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.enums.SyntaxType;
import in.costea.wiles.enums.WhenRemoveToken;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.OrderOfOperationsProcessor;
import in.costea.wiles.services.TokenTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.isContainedIn;
import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.*;
import static in.costea.wiles.statics.Utils.todo;

public abstract class AbstractExpressionCommand extends AbstractCommand {
    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(PREFIX_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
                    .withErrorMessage("Expected expression!").removeWhen(WhenRemoveToken.Never);
    protected final List<AbstractCommand> components = new ArrayList<>();
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    protected boolean shouldDoOrderOfOperations = true;
    protected boolean shouldFlatten = false;

    protected AbstractExpressionCommand(@NotNull TokenTransmitter transmitter) {
        super(transmitter);
    }

    @Override
    public @NotNull SyntaxType getType() {
        return SyntaxType.EXPRESSION;
    }

    @Override
    public @NotNull List<AbstractCommand> getComponents() {
        return components;
    }

    private void addInsideExpression(@NotNull AbstractExpressionCommand newExpression) throws AbstractCompilationException {
        @NotNull final var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        components.add(newExpression);
    }

    private @NotNull ExpectNext firstExpectNext(@NotNull String content) {
        if (IS_LITERAL.test(content) || BRACKETS.contains(content) || PREFIX_OPERATORS.contains(content))
            return ExpectNext.TOKEN;
        return ExpectNext.OPERATOR;
    }

    private @NotNull Token getNextToken(@NotNull ExpectNext expectNext) throws AbstractCompilationException {
        if (expectNext == ExpectNext.OPERATOR) return transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));

        return transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(PREFIX_OPERATORS))
                .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));
    }

    private boolean removeTrailingCommaIfExists() {
        if (components.size() > 0 && components.get(components.size() - 1) instanceof TokenCommand tokenCommand)
            if (tokenCommand.getToken().getContent().equals(COMMA_ID)) {
                components.remove(components.size() - 1);
                return true;
            }
        return false;
    }

    private void checkFinished(@NotNull ExpectNext expectNext, TokenLocation location) throws UnexpectedEndException {
        if (expectNext == ExpectNext.TOKEN && exceptions.size() == 0)
            if (!removeTrailingCommaIfExists())
                throw new UnexpectedEndException("Expression unfinished!", location);
    }

    private void addZeroIfNecessary(@NotNull String content, TokenLocation location) {
        if (ADD_ZERO_PREFIX_OPERATORS.contains(content))
            components.add(new TokenCommand(transmitter, new Token("#0", location)));
    }

    private void flatten() {
        if (components.size() == 1) {
            if (components.get(0) instanceof final AbstractExpressionCommand command && command.shouldFlatten) {
                components.clear();
                components.addAll(command.getComponents());
            }
        }
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainToken = transmitter.expect(START_OF_EXPRESSION);
            @NotNull ExpectNext expectNext;
            @NotNull var content = mainToken.getContent();
            TokenLocation location = mainToken.getLocation();
            expectNext = firstExpectNext(content);

            while (!transmitter.tokensExhausted()) {
                // finalize expression if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR))
                    if (checkExpressionFinalized())
                        break;

                // handle end token
                @NotNull final var tempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken.isPresent())
                    if (handleEndTokenReceived(tempToken.get().getLocation()))
                        break;

                //handle assignment token
                final var tempToken2 = transmitter.expectMaybe(tokenOf(ASSIGN_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken2.isPresent())
                    if (handleAssignTokenReceived(tempToken2.get().getLocation()))
                        break;

                // expect the next correct token
                mainToken = getNextToken(expectNext);
                content = mainToken.getContent();
                location = mainToken.getLocation();

                //handle closing brackets token
                if (content.equals(ROUND_BRACKET_END_ID) || content.equals(SQUARE_BRACKET_END_ID))
                    if (handleBracketsCloseTokenFound(content, location))
                        break;

                //method call
                if (expectNext == ExpectNext.OPERATOR && content.equals(ROUND_BRACKET_START_ID)) {
                    todo("Method call");
                }

                //applying square brackets to identifier
                if (content.equals(SQUARE_BRACKET_START_ID)) {
                    if (expectNext == ExpectNext.OPERATOR) {
                        addInsideExpression(new InsideSquareExpressionCommand(transmitter));
                        continue;
                    } else throw new UnexpectedTokenException("Identifier or unary operator expected!", location);
                }

                // switch expecting operator or token next
                if (expectNext == ExpectNext.TOKEN && PREFIX_OPERATORS.contains(content))
                    addZeroIfNecessary(content, location);
                else expectNext = expectNext == ExpectNext.OPERATOR ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

                // add inner expression
                if (content.equals(ROUND_BRACKET_START_ID))
                    addInsideExpression(new InsideRoundExpressionCommand(transmitter));
                else components.add(new TokenCommand(transmitter, mainToken));
            }

            checkBracketsCloseProperlyAtEnd(content, location);
            checkFinished(expectNext, location);

            if(shouldDoOrderOfOperations) {
                //Set order of operations
                @NotNull final var componentsAfterOOO = new OrderOfOperationsProcessor(transmitter, components).process();
                components.clear();
                components.addAll(componentsAfterOOO);
            }

            flatten();

        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }

    protected boolean handleAssignTokenReceived(TokenLocation location) throws AbstractCompilationException {
        throw new UnexpectedTokenException("Assignment not allowed here!", location);
    }

    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {
        //by default, there is no check
    }

    protected boolean handleEndTokenReceived(TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("End token not allowed here!", location);
    }

    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("Brackets don't close properly", location);
    }

    protected boolean checkExpressionFinalized() {
        return false;
    }

    private enum ExpectNext {
        OPERATOR,
        TOKEN
    }
}
