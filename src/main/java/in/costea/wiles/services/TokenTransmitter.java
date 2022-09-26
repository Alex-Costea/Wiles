package in.costea.wiles.services;

import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.UnexpectedEndException;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class TokenTransmitter
{
    private final LinkedList<Token> tokens;
    private final TokenLocation lastLocation;

    public TokenTransmitter(@NotNull List<Token> tokens)
    {
        this.tokens = new LinkedList<>(tokens);
        if (tokens.size() > 0)
            lastLocation = tokens.get(tokens.size() - 1).location();
        else lastLocation = new TokenLocation(0, 0);
    }

    public Token requestToken(String message) throws UnexpectedEndException
    {
        if (tokensExhausted()) throw new UnexpectedEndException(message, lastLocation);
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
