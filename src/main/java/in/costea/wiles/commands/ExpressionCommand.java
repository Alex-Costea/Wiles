package in.costea.wiles.commands;

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

public class ExpressionCommand extends AbstractCommand {
    @NotNull
    private final List<AbstractCommand> components = new ArrayList<>();
    @NotNull
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    @NotNull
    private final Token firstToken;
    @NotNull
    private final ExpressionType expressionType;
    @NotNull
    private ExpectNext expectNext=ExpectNext.INIT;

    public ExpressionCommand(@NotNull Token firstToken, @NotNull TokenTransmitter transmitter, @NotNull ExpressionType expressionType) {
        super(transmitter);
        this.expressionType = expressionType;
        this.firstToken = firstToken;
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


    //TODO: expression can start with (
    private void addInnerExpression(ExpressionType expressionType, boolean flatten) throws AbstractCompilationException {
        Token newToken = transmitter.expect(tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL)
                .or(isContainedIn(BRACKETS)).withErrorMessage("Identifier or unary operator expected!"));
        @NotNull
        var newExpression = new ExpressionCommand(newToken, transmitter, expressionType);
        @NotNull
        var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        if (!flatten || newExpression.components.size() > 1)
            components.add(newExpression);
        else components.add(newExpression.components.get(0));
    }

    // Method assumes first token is valid in an expression
    private void processFirstToken() throws AbstractCompilationException {
        boolean isUnaryOperator = UNARY_OPERATORS.contains(firstToken.getContent());
        boolean isOperator = isUnaryOperator || INFIX_OPERATORS.contains(firstToken.getContent());
        expectNext = isOperator ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

        if (isUnaryOperator)
            if (!firstToken.getContent().equals(NOT_ID))
                components.add(new TokenCommand(transmitter, new Token("" + NUM_START + "0", firstToken.getLocation())));
        components.add(new TokenCommand(transmitter, firstToken));

        String content = firstToken.getContent();
        if (content.equals(ROUND_BRACKET_START_ID)) {
            components.remove(0);
            addInnerExpression(ExpressionType.INSIDE_ROUND, true);
            expectNext = ExpectNext.OPERATOR;
        }

        if (content.equals(ROUND_BRACKET_END_ID) || content.equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedTokenException("Parentheses must have body!", firstToken.getLocation());
    }

    private void verifyOtherTokens() throws AbstractCompilationException {
        @NotNull
        Token mainToken = firstToken;
        while (!transmitter.tokensExhausted()) {
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
            var tempToken = transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeWhen(WhenRemoveToken.Never));
            if (tempToken.isPresent()) {
                if (expressionType == ExpressionType.RIGHT_SIDE)
                    break;
                else throw new UnexpectedTokenException("end", tempToken.get().getLocation());
            }

            if (expectNext == ExpectNext.OPERATOR)
                mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                        .or(isContainedIn(INFIX_OPERATORS)).withErrorMessage("Operator expected!"));
            else
                mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!").removeWhen(WhenRemoveToken.Always));

            if (mainToken.getContent().equals(ROUND_BRACKET_END_ID)) {
                if (expressionType == ExpressionType.INSIDE_ROUND) break; //end of inner statement
                else throw new UnexpectedTokenException("Brackets don't close properly", mainToken.getLocation());
            }
            if (mainToken.getContent().equals(SQUARE_BRACKET_END_ID)) {
                if (expressionType == ExpressionType.INSIDE_SQUARE) break; //end of inner statement
                else throw new UnexpectedTokenException("Brackets don't close properly", mainToken.getLocation());
            }

            if ((expectNext == ExpectNext.OPERATOR) && mainToken.getContent().equals(ROUND_BRACKET_START_ID)) {
                todo("Method call");
            }
            if (mainToken.getContent().equals(SQUARE_BRACKET_START_ID)) {
                addInnerExpression(ExpressionType.INSIDE_SQUARE, false);
                expectNext = ExpectNext.OPERATOR;
                continue;
            }

            if (expectNext == ExpectNext.TOKEN && UNARY_OPERATORS.contains(mainToken.getContent())) //unary +/-
                components.add(new TokenCommand(transmitter, new Token("#0", mainToken.getLocation())));
            else
                expectNext = expectNext == ExpectNext.OPERATOR ? ExpectNext.TOKEN : ExpectNext.OPERATOR;

            if (mainToken.getContent().equals(ROUND_BRACKET_START_ID)) //inner expression, not method call
                addInnerExpression(ExpressionType.INSIDE_ROUND, true);
            else components.add(new TokenCommand(transmitter, mainToken));
        }

        //verifying expression finished well
        if (expressionType == ExpressionType.INSIDE_ROUND && exceptions.size() == 0 && !mainToken.getContent().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", mainToken.getLocation());
        if (expressionType == ExpressionType.INSIDE_SQUARE && exceptions.size() == 0 && !mainToken.getContent().equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", mainToken.getLocation());
        if (expectNext == ExpectNext.TOKEN && exceptions.size() == 0) {
            //Ignore trailing comma
            if (components.size() > 0 && components.get(components.size() - 1) instanceof TokenCommand tokenCommand)
                if (tokenCommand.getToken().getContent().equals(COMMA_ID)) {
                    components.remove(components.size() - 1);
                    return;
                }
            throw new UnexpectedEndException("Expression unfinished!", mainToken.getLocation());
        }

        @NotNull
        final var componentsAfterOOO=new OrderOfOperationsProcessor(components).process();
        components.clear();
        components.addAll(componentsAfterOOO);
    }

    @Override
    public @NotNull CompilationExceptionsCollection process() {
        try {
            processFirstToken();
            verifyOtherTokens();
        } catch (AbstractCompilationException ex) {
            exceptions.add(ex);
        }
        return exceptions;
    }

    private enum ExpectNext {
        OPERATOR,
        TOKEN,
        INIT
    }
}
