package in.costea.wiles.services;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.UnexpectedEndException;

import java.util.LinkedList;
import java.util.List;

public class TokenTransmitter
{
    private final LinkedList<Token> tokens;

    public TokenTransmitter(List<Token> tokens)
    {
        this.tokens = new LinkedList<>(tokens);
    }

    public Token requestToken(String message) throws UnexpectedEndException
    {
        if (tokensExhausted()) throw new UnexpectedEndException(message);
        return tokens.getFirst();
    }

    public Token requestTokenAssertNotEmpty()
    {
        if (tokensExhausted()) throw new IllegalStateException("Input ended unexpectedly");
        return tokens.getFirst();
    }

    public void removeToken()
    {
        if (tokensExhausted()) throw new IllegalStateException("Tried removing token that didn't exist");
        tokens.pop();
    }

    public boolean tokensExhausted()
    {
        return tokens.isEmpty();
    }
}
