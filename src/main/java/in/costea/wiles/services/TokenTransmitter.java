package in.costea.wiles.services;

import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.UnexpectedEndException;

import java.util.LinkedList;
import java.util.List;

import static in.costea.wiles.statics.Constants.TOKENS_INVERSE;

public class TokenTransmitter {
    private final LinkedList<Token> tokens;

    public TokenTransmitter(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
    }

    public Token requestToken(String message)  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException(message);
        return tokens.getFirst();
    }

    public Token requestTokenAssertNotEmpty()
    {
        if(tokensExhausted()) throw new IllegalStateException("Input ended unexpectedly");
        return tokens.getFirst();
    }
    /**
     * @param expectedToken in case of UnexpectedEndException, what token was being expected?
     */
    public Token requestTokenExpecting(String expectedToken)  throws UnexpectedEndException
    {
        if(tokensExhausted()) throw new UnexpectedEndException("Missing token: \""+
                TOKENS_INVERSE.getOrDefault(expectedToken,"unknown token")+"\"");
        return tokens.getFirst();
    }

    public void removeToken()
    {
        if(tokensExhausted()) throw new IllegalStateException("Tried removing token that didn't exist");
        tokens.pop();
    }

    public boolean tokensExhausted(){return tokens.isEmpty();}
}
