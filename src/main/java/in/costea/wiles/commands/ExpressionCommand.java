package in.costea.wiles.commands;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.enums.ExpressionType;
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
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    @NotNull
    private final ExpressionType expressionType;

    public static final ExpectParamsBuilder START_OF_EXPRESSION =
            tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL).or(ROUND_BRACKET_START_ID)
            .withErrorMessage("Expected expression!").removeWhen(WhenRemoveToken.Never);

    public ExpressionCommand(@NotNull TokenTransmitter transmitter, @NotNull ExpressionType expressionType) {
        super(transmitter);
        this.expressionType = expressionType;
        switch (expressionType) {
            case INSIDE_ROUND -> name = "ROUND";
            case INSIDE_SQUARE -> name = "SQUARE";
        }
    }

    @Override
    public @NotNull SyntaxType getType() {
        return SyntaxType.EXPRESSION;
    }

    @Override
    public @NotNull List<AbstractCommand> getComponents() {
        return components;
    }


    private void addInnerExpression(ExpressionType expressionType) throws AbstractCompilationException {
        @NotNull
        final ExpressionCommand newExpression;
        switch(expressionType) {
            case INSIDE_ROUND -> newExpression = new InsideRoundBracketsExpressionCommand(transmitter);
            case INSIDE_SQUARE -> newExpression = new InsideSquareBracketsExpressionCommand(transmitter);
            default -> throw new IllegalArgumentException("Unknown expression type");
        }
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
                // finalize expression at newline/semicolon if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR) && expressionType == ExpressionType.RIGHT_SIDE &&
                        transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent())
                    break;

                // finalize expression at := if correctly finalized
                if ((expectNext == ExpectNext.OPERATOR) && expressionType == ExpressionType.LEFT_SIDE &&
                        transmitter.expectMaybe(tokenOf(ASSIGN_ID).removeWhen(WhenRemoveToken.Never)).isPresent())
                    break;

                // finalize expression at "end" if correct
                @NotNull
                final var tempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never));
                if (tempToken.isPresent()) {
                    if (expressionType == ExpressionType.RIGHT_SIDE)
                        break;
                    else throw new UnexpectedTokenException("end", tempToken.get().getLocation());
                }

                if (expectNext == ExpectNext.OPERATOR)
                    mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                            .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));
                else mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));

                content=mainToken.getContent();

                if (content.equals(ROUND_BRACKET_END_ID)) {
                    if (expressionType == ExpressionType.INSIDE_ROUND) break; //end of inner statement
                    else
                        throw new UnexpectedTokenException("Brackets don't close properly", mainToken.getLocation());
                }
                if (content.equals(SQUARE_BRACKET_END_ID)) {
                    if (expressionType == ExpressionType.INSIDE_SQUARE) break; //end of inner statement
                    else
                        throw new UnexpectedTokenException("Brackets don't close properly", mainToken.getLocation());
                }

                if (expectNext == ExpectNext.OPERATOR && content.equals(ROUND_BRACKET_START_ID)) {
                    todo("Method call");
                }

                if (content.equals(SQUARE_BRACKET_START_ID))
                {
                    if(expectNext == ExpectNext.OPERATOR)
                    {
                        addInnerExpression(ExpressionType.INSIDE_SQUARE);
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
                    addInnerExpression(ExpressionType.INSIDE_ROUND);
                else components.add(new TokenCommand(transmitter, mainToken));
            }

            //verifying expression finished well
            if (expressionType == ExpressionType.INSIDE_ROUND && exceptions.size() == 0 && !content.equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedEndException("Closing parentheses expected", mainToken.getLocation());
            if (expressionType == ExpressionType.INSIDE_SQUARE && exceptions.size() == 0 && !content.equals(SQUARE_BRACKET_END_ID))
                throw new UnexpectedEndException("Closing parentheses expected", mainToken.getLocation());

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

    private enum ExpectNext {
        OPERATOR,
        TOKEN
    }
}
