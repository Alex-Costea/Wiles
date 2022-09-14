package in.costea.wiles.services;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.UnexpectedEndException;

import java.util.LinkedList;
import java.util.List;

public class TokenTransmitter {
    private final LinkedList<Token> tokens;

    public TokenTransmitter(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
    }

    public Token requestToken()  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException("Input ended unexpectedly!");
        return tokens.getFirst();
    }

    public void removeToken()  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException("Input ended unexpectedly!");
        tokens.pop();
    }

    public boolean tokensExhausted(){return tokens.isEmpty();}
}
