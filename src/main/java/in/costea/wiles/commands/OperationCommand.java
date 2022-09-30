package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.statics.Constants.*;

public class OperationCommand extends AbstractOperationComponentCommand
{
    private final List<AbstractOperationComponentCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final boolean innerOperation;
    private final Token firstToken;

    public OperationCommand(Token firstToken, TokenTransmitter transmitter, boolean innerOperation)
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
    public List<AbstractOperationComponentCommand> getComponents()
    {
        return components;
    }

    private void addInnerOperation() throws CompilationException
    {
        Token newToken = transmitter.expect(anyToken().withErrorMessage( "Unexpected operation end!"));
        var newOperation = new OperationCommand(newToken, transmitter, true);
        var newExceptions = newOperation.process();
        if (newExceptions.size() > 0)
            throw newExceptions.get(0);
        if (newOperation.components.size() > 1)
            components.add(newOperation);
        else components.add(newOperation.components.get(0));
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            //verifying the first token
            //Token token = firstToken;

            boolean isOperator=ALLOWED_OPERATORS_IN_OPERATION.contains(firstToken.content());
            boolean expectOperatorNext = !isOperator;
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

            if (content.equals(FINISH_STATEMENT_ID))
                throw new UnexpectedTokenException(TOKENS_INVERSE.get(FINISH_STATEMENT_ID), firstToken.location());

            if (content.equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedTokenException("Parentheses must have body!", firstToken.location());

            if(isOperator && content.startsWith(IDENTIFIER_START))
                throw new UnexpectedTokenException("Identifier expected!",firstToken.location());


            Token token=firstToken;
            //verifying other tokens
            while (!transmitter.tokensExhausted())
            {
                token = transmitter.expect(requestFirstToken);
                content = token.content();

                if (content.equals(END_BLOCK_ID) && !innerOperation) //method end statement
                    break;

                if (content.equals(END_BLOCK_ID))//end statement in inner operation
                {
                    transmitter.removeToken();
                    throw new UnexpectedTokenException("end", token.location());
                }

                if (expectOperatorNext && !innerOperation && STATEMENT_ENDERS.contains(content))
                    break; //finalize operation

                if (content.equals(FINISH_STATEMENT_ID))
                {
                    transmitter.removeToken();
                    throw new UnexpectedTokenException(";", token.location());
                }

                if (expectOperatorNext)
                    token = transmitter.expect(tokenOf(x -> ROUND_BRACKETS.contains(x) || ALLOWED_OPERATORS_IN_OPERATION.contains(x))
                            .withErrorMessage("Operator expected!"));
                else
                    token = transmitter.expect(tokenOf(x -> ROUND_BRACKETS.contains(x) || UNARY_OPERATORS.contains(x) ||
                            x.startsWith("!") || x.startsWith("@") || x.startsWith("#"))
                            .withErrorMessage("Identifier or unary operator expected!"));

                if (token.content().equals(ROUND_BRACKET_END_ID))
                {
                    if (innerOperation) break; //end of inner statement
                    else throw new UnexpectedTokenException("Extra parentheses found", token.location());
                }

                if (expectOperatorNext && token.content().equals(ROUND_BRACKET_START_ID))
                {
                    //TODO: implement
                    throw new Error("Method call not yet implemented!");
                }

                if (!expectOperatorNext && UNARY_OPERATORS.contains(token.content())) //unary operation
                    components.add(new TokenCommand(transmitter, new Token("#0", token.location())));
                else
                    expectOperatorNext = !expectOperatorNext; //toggle operators and identifiers

                if (token.content().equals(ROUND_BRACKET_START_ID)) //inner operation, not method call
                    addInnerOperation();
                else components.add(new TokenCommand(transmitter, token));
            }

            //verifying operation finished well
            if (innerOperation && exceptions.size() == 0 && !token.content().equals(ROUND_BRACKET_END_ID))
                throw new UnexpectedEndException("Closing parentheses expected", token.location());
            if (!expectOperatorNext && exceptions.size() == 0)
                throw new UnexpectedEndException("Operation unfinished!", token.location());

            //TODO: process order of operations and check if effect exists
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
