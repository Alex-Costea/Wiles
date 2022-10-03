package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.AbstractCompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.statics.Constants.*;

public class ExpressionCommand extends AbstractCommand {
    private final List<AbstractCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final Token firstToken;
    private final ExpressionType expressionType;
    private ExpectNext expectNext;

    public static final ExpressionType REGULAR= ExpressionType.REGULAR;
    public static final ExpressionType INSIDE_ROUND= ExpressionType.INSIDE_ROUND;
    public static final ExpressionType INSIDE_SQUARE= ExpressionType.INSIDE_SQUARE;

    private enum ExpressionType
    {
        REGULAR,
        INSIDE_ROUND,
        INSIDE_SQUARE
    }

    private enum ExpectNext
    {
        OPERATOR,
        TOKEN
    }

    public ExpressionCommand(Token firstToken, TokenTransmitter transmitter, ExpressionType expressionType)
    {
        super(transmitter);
        this.expressionType = expressionType;
        this.firstToken = firstToken;
        switch(expressionType)
        {
            case INSIDE_ROUND -> name="ROUND";
            case INSIDE_SQUARE -> name="SQUARE";
        }
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.EXPRESSION;
    }

    @Override
    public List<AbstractCommand> getComponents()
    {
        return components;
    }

    private void addInnerExpression(ExpressionType expressionType, boolean flatten) throws AbstractCompilationException
    {
        Token newToken = transmitter.expect(tokenOf(isContainedIn(UNARY_OPERATORS)).or(IS_LITERAL)
                .or(isContainedIn(BRACKETS)).removeTokenWhen(ALWAYS)
                .withErrorMessage("Identifier or unary operator expected!"));
        var newExpression = new ExpressionCommand(newToken, transmitter, expressionType);
        var newExceptions = newExpression.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        if (!flatten || newExpression.components.size() > 1)
            components.add(newExpression);
        else components.add(newExpression.components.get(0));
    }

    // Method assumes first token is valid in an expression
    private void processFirstToken() throws AbstractCompilationException
    {
        boolean isUnaryOperator = UNARY_OPERATORS.contains(firstToken.content());
        boolean isOperator =  isUnaryOperator || INFIX_OPERATORS.contains(firstToken.content());
        expectNext = isOperator?ExpectNext.TOKEN:ExpectNext.OPERATOR;

        if (isUnaryOperator)
            if (!firstToken.content().equals(NOT_ID))
                components.add(new TokenCommand(transmitter, new Token("" + NUM_START + "0", firstToken.location())));
        components.add(new TokenCommand(transmitter, firstToken));

        String content = firstToken.content();
        if (content.equals(ROUND_BRACKET_START_ID))
        {
            components.remove(0);
            addInnerExpression(INSIDE_ROUND,true);
            expectNext = ExpectNext.OPERATOR;
        }

        if (content.equals(ROUND_BRACKET_END_ID) || content.equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedTokenException("Parentheses must have body!", firstToken.location());
    }

    private void verifyOtherTokens() throws AbstractCompilationException
    {
        Token mainToken=firstToken;
        while (!transmitter.tokensExhausted())
        {
            // finalize expression at newline/semicolon if correctly finalized
            if ((expectNext==ExpectNext.OPERATOR) && expressionType == REGULAR &&
                    transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent())
                break;

            // finalize expression at "end" if correct
            var tempToken=transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeTokenWhen(NEVER));
            if (tempToken.isPresent()) {
                if (expressionType == REGULAR)
                    break;
                else throw new UnexpectedTokenException("end", tempToken.get().location());
            }

            tempToken=transmitter.expectMaybe(tokenOf(STATEMENT_TERMINATOR_ID));
            if (tempToken.isPresent())
                throw new UnexpectedTokenException(";", tempToken.get().location());


            if (expectNext==ExpectNext.OPERATOR)
                mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS))
                        .or(isContainedIn(INFIX_OPERATORS))
                        .withErrorMessage("Operator expected!").removeTokenWhen(ALWAYS));
            else
                mainToken = transmitter.expect(tokenOf(isContainedIn(BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!").removeTokenWhen(ALWAYS));

            var ex=new UnexpectedTokenException("Brackets don't close properly", mainToken.location());
            if (mainToken.content().equals(ROUND_BRACKET_END_ID))
            {
                if (expressionType == INSIDE_ROUND) break; //end of inner statement
                else throw ex;
            }
            if (mainToken.content().equals(SQUARE_BRACKET_END_ID))
            {
                if (expressionType == INSIDE_SQUARE) break; //end of inner statement
                else throw ex;
            }

            if ((expectNext==ExpectNext.OPERATOR) && mainToken.content().equals(ROUND_BRACKET_START_ID))
            {
                //TODO: implement
                throw new Error("Method call not yet implemented!");
            }
            if (mainToken.content().equals(SQUARE_BRACKET_START_ID))
            {
                addInnerExpression(INSIDE_SQUARE,false);
                expectNext = ExpectNext.OPERATOR;
                continue;
            }

            if (expectNext == ExpectNext.TOKEN && UNARY_OPERATORS.contains(mainToken.content())) //unary +/-
                components.add(new TokenCommand(transmitter, new Token("#0", mainToken.location())));
            else
                expectNext = expectNext==ExpectNext.OPERATOR?ExpectNext.TOKEN:ExpectNext.OPERATOR;

            if (mainToken.content().equals(ROUND_BRACKET_START_ID)) //inner expression, not method call
                addInnerExpression(INSIDE_ROUND,true);
            else components.add(new TokenCommand(transmitter, mainToken));
        }

        //verifying expression finished well
        if (expressionType == INSIDE_ROUND && exceptions.size() == 0 && !mainToken.content().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", mainToken.location());
        if (expressionType == INSIDE_SQUARE && exceptions.size() == 0 && !mainToken.content().equals(SQUARE_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", mainToken.location());
        if (expectNext==ExpectNext.TOKEN && exceptions.size() == 0)
        {
            if(components.size()>0 && components.get(components.size()-1) instanceof TokenCommand tokenCommand)
                if (tokenCommand.getToken().content().equals(COMMA_ID))
                {
                    components.remove(components.size() - 1);
                    return;
                }
            throw new UnexpectedEndException("Expression unfinished!", mainToken.location());
        }
        //TODO: process order of operations and check if effect exists
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            processFirstToken();
            verifyOtherTokens();
        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
