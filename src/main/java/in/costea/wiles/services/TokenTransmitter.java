package in.costea.wiles.services;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static in.costea.wiles.builders.ExpectParamsBuilder.*;
import static in.costea.wiles.statics.Constants.*;

public class TokenTransmitter
{
    private final LinkedList<Token> tokens;
    private final TokenLocation lastLocation;

    public TokenTransmitter(@NotNull List<Token> tokens)
    {
        this.tokens = new LinkedList<>(tokens);
        if (tokens.size() > 0)
            lastLocation = tokens.get(tokens.size() - 1).location();
        else lastLocation = null;
    }

    private void removeToken() {
        if (tokensExhausted())
            throw new IllegalStateException("Tokens exhausted!");
        tokens.pop();
    }

    public boolean tokensExhausted()
    {
        return tokens.isEmpty();
    }

    public Token expect(ExpectParamsBuilder params) throws UnexpectedEndException, UnexpectedTokenException, TokenExpectedException {
        String message = params.getErrorMessage();
        boolean succeeded = false;
        if(params.getWhenRemoveToken()==DEFAULT)
            params.removeTokenWhen(ALWAYS);
        try
        {
            if(tokensExhausted())
                throw new UnexpectedEndException(message,lastLocation);
            Token token;

            boolean shouldIgnoreNewLine = params.isIgnoringNewLine();
            if(shouldIgnoreNewLine)
            {
                while ((token=tokens.getFirst()).content().equals(NEWLINE_ID))
                {
                    if (tokensExhausted())
                        throw new UnexpectedEndException(message, token.location());
                    removeToken();
                }
            }

            token=tokens.getFirst();
            Predicate<String> foundTest= params.getFoundTest();
            if (!foundTest.test(token.content()))
                throw new TokenExpectedException(message, token.location());

            succeeded=true;
            return token;
        }
        finally
        {
            var whenRemoveToken = params.getWhenRemoveToken();
            if((!succeeded && whenRemoveToken == ALWAYS) || (succeeded && whenRemoveToken != NEVER))
                if(!tokensExhausted())
                    removeToken();
        }
    }

    public Optional<Token> expectMaybe(ExpectParamsBuilder expectParamsBuilder) throws UnexpectedTokenException
    {
        try
        {
            if(expectParamsBuilder.getWhenRemoveToken()==DEFAULT)
                expectParamsBuilder.removeTokenWhen(WHEN_FOUND);
            return Optional.of(expect(expectParamsBuilder));
        }
        catch (TokenExpectedException | UnexpectedEndException ex)
        {
            return Optional.empty();
        }
    }
}
