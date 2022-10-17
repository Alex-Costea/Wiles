package in.costea.wiles.commands.expressionCommands;

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

public abstract class ExpressionCommand extends AbstractCommand {
    @NotNull
    private final List<AbstractCommand> components = new ArrayList<>();
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
            .withErrorMessage("Expected expression!").removeWhen(WhenRemoveToken.Never);

    protected ExpressionCommand(@NotNull TokenTransmitter transmitter) {
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


    private void addInsideRoundExpression() throws AbstractCompilationException {
        @NotNull
        final ExpressionCommand newExpression = new InsideRoundBracketsExpressionCommand(transmitter);
        @NotNull
        final var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        components.add(newExpression);
    }

    private void addInsideSquareExpression() throws AbstractCompilationException {
        @NotNull
        final ExpressionCommand newExpression = new InsideSquareBracketsExpressionCommand(transmitter);
        @NotNull
        final var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        components.add(newExpression);
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull
            Token mainToken=transmitter.expect(START_OF_EXPRESSION);
            @NotNull ExpectNext expectNext;
            var content=mainToken.getContent();
            if(IS_LITERAL.test(content) || BRACKETS.contains(content) || UNARY_OPERATORS.contains(content))
                expectNext = ExpectNext.TOKEN;
            else expectNext = ExpectNext.OPERATOR;

            while (!transmitter.tokensExhausted())
            {
                // finalize expression if correctly finalized
                if((expectNext==ExpectNext.OPERATOR) && transmitter.expectMaybe(expressionFinalized()).isPresent())
                    break;

                // handle end token
                @NotNull
                final var tempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken.isPresent())
                    if(handleEndTokenReceived(tempToken.get()))
                        break;

                if (expectNext == ExpectNext.OPERATOR)
                    mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                            .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));
                else mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));

                content=mainToken.getContent();

                if(handleBracketsCloseTokenFound(content,mainToken.getLocation()))
                    break;

                if (expectNext == ExpectNext.OPERATOR && content.equals(ROUND_BRACKET_START_ID)) {
                    todo("Method call");
                }

                if (content.equals(SQUARE_BRACKET_START_ID))
                {
                    if(expectNext == ExpectNext.OPERATOR)
                    {
                        addInsideSquareExpression();
                        continue;
                    }
                    else throw new UnexpectedTokenException("Identifier or unary operator expected!",mainToken.getLocation());
                }

                if (expectNext == ExpectNext.TOKEN && UNARY_OPERATORS.contains(content))
                {
                    if (ADD_ZERO_UNARY_OPERATORS.contains(content))
                        components.add(new TokenCommand(transmitter, new Token("#0", mainToken.getLocation())));
                }
                else expectNext = expectNext == ExpectNext.OPERATOR ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

                if (content.equals(ROUND_BRACKET_START_ID)) //inner expression, not method call
                    addInsideRoundExpression();
                else components.add(new TokenCommand(transmitter, mainToken));
            }

            //verifying expression finished well
            if(exceptions.size()==0)
                checkBracketsCloseProperlyAtEnd(content,mainToken.getLocation());


            //Ignore trailing comma
            if (expectNext == ExpectNext.TOKEN && exceptions.size() == 0) {
                if (components.size() > 0 && components.get(components.size() - 1) instanceof TokenCommand tokenCommand)
                {
                    if (tokenCommand.getToken().getContent().equals(COMMA_ID))
                        components.remove(components.size() - 1);
                    else throw new UnexpectedEndException("Expression unfinished!", mainToken.getLocation());
                }
            }

            //Flatten
            if(components.size()==1)
            {
                if(components.get(0) instanceof final ExpressionCommand expressionCommand && components.get(0).name.equals("ROUND"))
                {
                    components.clear();
                    components.addAll(expressionCommand.components);
                }
            }

            //Set order of operations
            @NotNull
            final var componentsAfterOOO = new OrderOfOperationsProcessor(components).process();
            components.clear();
            components.addAll(componentsAfterOOO);

        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }

    protected void checkBracketsCloseProperlyAtEnd(String content, TokenLocation location) throws UnexpectedEndException {
        //by default, there is no check
    }

    protected boolean handleEndTokenReceived(Token token) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("end", token.getLocation());
    }

    protected boolean handleBracketsCloseTokenFound(String content, TokenLocation location) throws UnexpectedTokenException {
        if(content.equals(ROUND_BRACKET_END_ID) || content.equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedTokenException("Brackets don't close properly", location);
        return false;
    }

    protected ExpectParamsBuilder expressionFinalized()
    {
        return tokenOf((x)->false).removeWhen(WhenRemoveToken.Never);
    }

    private enum ExpectNext {
        OPERATOR,
        TOKEN
    }
}
