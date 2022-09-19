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

import static in.costea.wiles.statics.Constants.*;

public class OperationCommand extends SyntaxTree {
    private final List<SyntaxTree> components=new ArrayList<>();
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    private boolean expectOperatorNext;
    private final boolean mustHaveEffect;
    private final boolean insideParen;
    //private final Token firstToken;

    public OperationCommand(Token firstToken, TokenTransmitter transmitter,boolean mustHaveEffect,boolean insideParen) {
        super(transmitter);
        this.mustHaveEffect = mustHaveEffect;
        this.insideParen = insideParen;
        components.add(new TokenCommand(transmitter,firstToken));
        expectOperatorNext = !OPERATORS.containsValue(firstToken.content());
    }

    @Override
    public Constants.SYNTAX_TYPE getType() {
        return SYNTAX_TYPE.OPERATION;
    }

    @Override
    public List<SyntaxTree> getComponents() {
        return components;
    }

    private void addInnerOperation() throws UnexpectedEndException {
        Token newToken=transmitter.requestToken("Parentheses must have content!");
        transmitter.removeToken();
        var newOperation=new OperationCommand(newToken,
                transmitter,false,true);
        exceptions.add(newOperation.process());
        if(newOperation.components.size()>1)
            components.add(newOperation);
        else components.add(newOperation.components.get(0));
    }

    @Override
    public CompilationExceptionsCollection process() {
        Token token=null;
        SyntaxTree firstComponent=components.get(0);
        try
        {
            if (firstComponent instanceof TokenCommand firstTokenCommand)
            {
                if (firstTokenCommand.getContent().equals(ROUND_BRACKET_START_ID))
                {
                    components.remove(0);
                    addInnerOperation();
                    expectOperatorNext=true;
                }
                if (firstTokenCommand.getContent().equals(ROUND_BRACKET_END_ID))
                {
                    throw new UnexpectedTokenException("Parentheses must have body!",firstTokenCommand.getToken().location());
                }
            }
        }
        catch(CompilationException ex)
        {
            exceptions.add(ex);
            return exceptions;
        }
        while(!transmitter.tokensExhausted())
        {
            try
            {
                token=transmitter.requestToken("Token expected!");
                String content=token.content();
                if (content.equals(END_BLOCK_ID))
                    break;
                if(expectOperatorNext && (content.equals(FINISH_STATEMENT) || content.equals(NEWLINE_ID)))
                    break;
                if (expectOperatorNext)
                {
                    token = expect(x->OPERATORS.containsValue(x) || x.equals(ROUND_BRACKET_START_ID)
                            || x.equals(ROUND_BRACKET_END_ID), "Operator expected!");
                    if(token.content().equals(ROUND_BRACKET_END_ID))
                    {
                        if (insideParen) break;
                        else throw new UnexpectedTokenException("Extra parentheses found", token.location());
                    }
                    if(token.content().equals(ROUND_BRACKET_START_ID))
                        throw new Error("Method call not yet implemented!");
                    expectOperatorNext = false;
                }
                else
                {
                    token = expect((String x) ->x.equals(ROUND_BRACKET_START_ID) || x.equals(ROUND_BRACKET_END_ID) ||
                            x.startsWith("!") || x.startsWith("@") || x.startsWith("#"), "Identifier expected!");
                    if(token.content().equals(ROUND_BRACKET_END_ID))
                    {
                        if (insideParen) break;
                        else throw new UnexpectedTokenException("Extra parentheses found", token.location());
                    }
                    expectOperatorNext = true;
                    if(token.content().equals(ROUND_BRACKET_START_ID))
                    {
                        addInnerOperation();
                        continue;
                    }
                }
                components.add(new TokenCommand(transmitter,token));
            }
            catch(CompilationException ex)
            {
                exceptions.add(ex);
                break;
            }
        }
        if(insideParen && (token == null || !token.content().equals(ROUND_BRACKET_END_ID)))
            exceptions.add(new UnexpectedEndException("Closing parentheses expected"));
        if(!expectOperatorNext && exceptions.size()==0)
            exceptions.add(new UnexpectedEndException("Operation unfinished!"));
        //TODO: process order of operations and check if effect exists
        return exceptions;
    }
}
