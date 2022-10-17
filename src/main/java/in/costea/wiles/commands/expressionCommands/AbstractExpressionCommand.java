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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.statics.Constants.*;
import static in.costea.wiles.statics.Utils.todo;

public abstract class AbstractExpressionCommand extends AbstractCommand {
    @NotNull
    protected final List<AbstractCommand> components = new ArrayList<>();
    @NotNull
    protected final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
            .withErrorMessage("Expected expression!").removeWhen(WhenRemoveToken.Never);

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

    private void addInsideExpression(AbstractExpressionCommand newExpression) throws AbstractCompilationException
    {
        @NotNull
        final var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        components.add(newExpression);
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            @NotNull Token mainToken=transmitter.expect(START_OF_EXPRESSION);
            @NotNull ExpectNext expectNext;
            @NotNull var content=mainToken.getContent();
            @Nullable TokenLocation location = mainToken.getLocation();

            //starting with token or operator?
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
                    if(handleEndTokenReceived(tempToken.get().getLocation()))
                        break;

                //handle assignment token
                final var tempToken2 = transmitter.expectMaybe(tokenOf(ASSIGN_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken2.isPresent())
                    if(handleAssignTokenReceived(tempToken2.get().getLocation()))
                        break;

                // expect the next correct token
                if (expectNext == ExpectNext.OPERATOR)
                    mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                            .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));
                else mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));

                //reassign Token information
                content=mainToken.getContent();
                location =mainToken.getLocation();

                //handle closing brackets token
                if(content.equals(ROUND_BRACKET_END_ID) || content.equals(SQUARE_BRACKET_END_ID))
                    if(handleBracketsCloseTokenFound(content,location))
                        break;

                //method call
                if (expectNext == ExpectNext.OPERATOR && content.equals(ROUND_BRACKET_START_ID)) {
                    todo("Method call");
                }

                //applying square brackets to identifier
                if (content.equals(SQUARE_BRACKET_START_ID))
                {
                    if(expectNext == ExpectNext.OPERATOR)
                    {
                        addInsideExpression(new InsideSquareExpressionCommand(transmitter));
                        continue;
                    }
                    else throw new UnexpectedTokenException("Identifier or unary operator expected!",location);
                }

                // add "0" if necessary
                if (expectNext == ExpectNext.TOKEN && UNARY_OPERATORS.contains(content))
                {
                    if (ADD_ZERO_UNARY_OPERATORS.contains(content))
                        components.add(new TokenCommand(transmitter, new Token("#0", location)));
                }
                // switch expecting operator or token next
                else expectNext = expectNext == ExpectNext.OPERATOR ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

                // add inner expression
                if (content.equals(ROUND_BRACKET_START_ID))
                    addInsideExpression(new InsideRoundExpressionCommand(transmitter));
                else components.add(new TokenCommand(transmitter, mainToken));
            }

            //verifying expression finished well
            if(exceptions.size()==0)
                checkBracketsCloseProperlyAtEnd(content,location);


            //Ignore trailing comma
            if (expectNext == ExpectNext.TOKEN && exceptions.size() == 0) {
                if (components.size() > 0 && components.get(components.size() - 1) instanceof TokenCommand tokenCommand)
                {
                    if (tokenCommand.getToken().getContent().equals(COMMA_ID))
                        components.remove(components.size() - 1);
                    else throw new UnexpectedEndException("Expression unfinished!", location);
                }
            }

            //Flatten
            if(components.size()==1)
            {
                if(components.get(0) instanceof final InsideRoundExpressionCommand expressionCommand)
                {
                    components.clear();
                    components.addAll(expressionCommand.getComponents());
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

    protected boolean handleAssignTokenReceived(TokenLocation location) throws AbstractCompilationException {
        throw new UnexpectedTokenException("Assignment not allowed here!",location);
    }

    protected void checkBracketsCloseProperlyAtEnd(@NotNull String content, TokenLocation location) throws UnexpectedEndException {
        //by default, there is no check
    }

    protected boolean handleEndTokenReceived(TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("end", location);
    }

    protected boolean handleBracketsCloseTokenFound(@NotNull String content, TokenLocation location) throws UnexpectedTokenException {
        throw new UnexpectedTokenException("Brackets don't close properly", location);
    }

    protected ExpectParamsBuilder expressionFinalized()
    {
        return tokenOf(NOTHING).removeWhen(WhenRemoveToken.Never).dontIgnoreNewLine();
    }

    private enum ExpectNext {
        OPERATOR,
        TOKEN
    }
}
