package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.*;

public class OperationCommand extends AbstractOperationComponent
{
    private final List<AbstractOperationComponent> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();
    private final boolean innerOperation;
    private final Token firstToken;
    private boolean expectOperatorNext;

    public OperationCommand(Token firstToken, TokenTransmitter transmitter, boolean innerOperation)
    {
        super(transmitter);
        this.innerOperation = innerOperation;
        this.firstToken = firstToken;
        components.add(new TokenCommand(transmitter, firstToken));
        expectOperatorNext = !OPERATORS.containsValue(firstToken.content());
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return SYNTAX_TYPE.OPERATION;
    }

    @Override
    public List<AbstractOperationComponent> getComponents()
    {
        return components;
    }

    private void addInnerOperation() throws CompilationException
    {
        Token newToken = expect((x) -> true, "Parentheses must have body!");
        var newOperation = new OperationCommand(newToken, transmitter, true);
        var newExceptions = newOperation.process();
        if(newExceptions.size()>0)
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
            Token token = firstToken;
            if (components.get(0) instanceof TokenCommand firstComponent)
            {
                if (firstComponent.getContent().equals(ROUND_BRACKET_START_ID))
                {
                    components.remove(0);
                    addInnerOperation();
                    expectOperatorNext = true;
                }
                if (firstComponent.getContent().equals(ROUND_BRACKET_END_ID))
                {
                    throw new UnexpectedTokenException("Parentheses must have body!", firstComponent.getToken().location());
                }
            }
            while (!transmitter.tokensExhausted())
            {
                token = transmitter.requestToken("");
                String content = token.content();
                if (content.equals(END_BLOCK_ID) && !innerOperation) //method end statement
                    break;
                if(content.equals(END_BLOCK_ID)) //end statement in inner operation
                    throw new UnexpectedTokenException("end",token.location());
                if (expectOperatorNext && !innerOperation && (content.equals(FINISH_STATEMENT) || content.equals(NEWLINE_ID)))
                    break; //finalize operation
                if(content.equals(FINISH_STATEMENT))
                    throw new UnexpectedTokenException(";",token.location());
                if(expectOperatorNext)
                    token = expect(x -> OPERATORS.containsValue(x) || x.equals(ROUND_BRACKET_START_ID)
                            || x.equals(ROUND_BRACKET_END_ID), "Operator expected!");
                else
                    token = expect(x -> x.equals(ROUND_BRACKET_START_ID) || x.equals(ROUND_BRACKET_END_ID) ||
                            x.startsWith("!") || x.startsWith("@") || x.startsWith("#"), "Identifier expected!");
                if (token.content().equals(ROUND_BRACKET_END_ID))
                {
                    if (innerOperation) break; //end of inner statement
                    else throw new UnexpectedTokenException("Extra parentheses found", token.location());
                }
                if (expectOperatorNext && token.content().equals(ROUND_BRACKET_START_ID))
                    //TODO: implement
                    throw new Error("Method call not yet implemented!");
                expectOperatorNext=!expectOperatorNext;
                if (token.content().equals(ROUND_BRACKET_START_ID)) //inner operation, not method call
                    addInnerOperation();
                else components.add(new TokenCommand(transmitter, token));
            }

            if (innerOperation && exceptions.size() == 0 &&  !token.content().equals(ROUND_BRACKET_END_ID))
                exceptions.add(new UnexpectedEndException("Closing parentheses expected", token.location()));
            if (!expectOperatorNext && exceptions.size() == 0)
                exceptions.add(new UnexpectedEndException("Operation unfinished!", token.location()));
            //TODO: process order of operations and check if effect exists
        } catch (CompilationException ex)
        {
            exceptions.add(ex);
            if (ex instanceof TokenExpectedException)
                transmitter.removeToken();
        }
        return exceptions;
    }
}
