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
    private final List<ExpressionCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final boolean innerOperation;
    private final Token firstToken;
    private boolean expectOperatorNext;

    public ExpressionCommand(Token firstToken, TokenTransmitter transmitter, boolean innerOperation)
    {
        super(transmitter);
        this.innerOperation = innerOperation;
        this.firstToken = firstToken;

    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.OPERATION;
    }

    @Override
    public List<ExpressionCommand> getComponents()
    {
        return components;
    }

    private void addInnerOperation() throws AbstractCompilationException
    {
        Token newToken = transmitter.expect(tokenOf(ANYTHING).withErrorMessage( "Unexpected operation end!"));
        var newOperation = new ExpressionCommand(newToken, transmitter, true);
        var newExceptions = newOperation.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        if (newOperation.components.size() > 1)
            components.add(newOperation);
        else components.add(newOperation.components.get(0));
    }

    private void verifyOtherTokens() throws AbstractCompilationException
    {
        Token mainToken=firstToken;
        while (!transmitter.tokensExhausted())
        {
            if (expectOperatorNext && !innerOperation &&
                    transmitter.expectMaybe(tokenOf(isContainedIn(STATEMENT_TERMINATORS)).dontIgnoreNewLine()).isPresent())
                break; //finalize operation

            var tempToken=transmitter.expectMaybe(tokenOf(END_BLOCK_ID).removeTokenWhen(NEVER));
            if (tempToken.isPresent()) {
                if (!innerOperation)
                    break;
                else throw new UnexpectedTokenException("end", tempToken.get().location());
            }

            tempToken=transmitter.expectMaybe(tokenOf(STATEMENT_TERMINATOR_ID));
            if (tempToken.isPresent())
                throw new UnexpectedTokenException(";", tempToken.get().location());


            if (expectOperatorNext)
                mainToken = transmitter.expect(tokenOf(isContainedIn(ROUND_BRACKETS))
                        .or(isContainedIn(ALLOWED_OPERATORS_IN_OPERATION))
                        .withErrorMessage("Operator expected!"));
            else
                mainToken = transmitter.expect(tokenOf(isContainedIn(ROUND_BRACKETS)).or(isContainedIn(UNARY_OPERATORS))
                        .or(IS_LITERAL).withErrorMessage("Identifier or unary operator expected!"));

            if (mainToken.content().equals(ROUND_BRACKET_END_ID))
            {
                if (innerOperation) break; //end of inner statement
                else throw new UnexpectedTokenException("Extra parentheses found", mainToken.location());
            }

            if (expectOperatorNext && mainToken.content().equals(ROUND_BRACKET_START_ID))
            {
                //TODO: implement
                throw new Error("Method call not yet implemented!");
            }

            if (!expectOperatorNext && UNARY_OPERATORS.contains(mainToken.content())) //unary operation
                components.add(new TokenCommand(transmitter, new Token("#0", mainToken.location())));
            else
                expectOperatorNext = !expectOperatorNext; //toggle operators and identifiers

            if (mainToken.content().equals(ROUND_BRACKET_START_ID)) //inner operation, not method call
                addInnerOperation();
            else components.add(new TokenCommand(transmitter, mainToken));
        }

        //verifying operation finished well
        if (innerOperation && exceptions.size() == 0 && !mainToken.content().equals(ROUND_BRACKET_END_ID))
            throw new UnexpectedEndException("Closing parentheses expected", mainToken.location());
        if (!expectOperatorNext && exceptions.size() == 0)
            throw new UnexpectedEndException("Operation unfinished!", mainToken.location());

        //TODO: process order of operations and check if effect exists
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            //verifying the first token
            boolean isOperator=ALLOWED_OPERATORS_IN_OPERATION.contains(firstToken.content());
            expectOperatorNext = !isOperator;
            if (!expectOperatorNext)
                components.add(new TokenCommand(transmitter, new Token("" + NUM_START + "0", firstToken.location())));
            components.add(new TokenCommand(transmitter, firstToken));

            String content = firstToken.content();
            if (content.equals(ROUND_BRACKET_START_ID))
            {
                components.remove(0);
                addInnerOperation();
                expectOperatorNext = true;
            }

            if (content.equals(STATEMENT_TERMINATOR_ID))
                throw new UnexpectedTokenException(TOKENS_INVERSE.get(STATEMENT_TERMINATOR_ID), firstToken.location());

            if (content.equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedTokenException("Parentheses must have body!", firstToken.location());

            if(isOperator && content.startsWith(IDENTIFIER_START))
                throw new UnexpectedTokenException("Identifier expected!",firstToken.location());

            verifyOtherTokens();

        }
        catch (AbstractCompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
