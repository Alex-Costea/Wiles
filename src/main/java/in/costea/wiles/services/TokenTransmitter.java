package in.costea.wiles.services;

import in.costea.wiles.builders.ExpectParamsBuilder;
import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static in.costea.wiles.builders.ExpectParamsBuilder.ALWAYS;
import static in.costea.wiles.builders.ExpectParamsBuilder.NEVER;
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
        else lastLocation = new TokenLocation(0, 0);
    }

    public void readUntilIgnoringErrors(Predicate<String> stop)
    {
        Token token;
        try
        {
            while(true)
            {
                token = requestToken("");
                if (stop.test(token.content()))
                    return;
                removeToken();
            }
        }
        catch (UnexpectedEndException ignored)
        {
        }
    }

    private Token requestToken(String message) throws UnexpectedEndException
    {
        if (tokensExhausted()) throw new UnexpectedEndException(message, lastLocation);
        return tokens.getFirst();
    }

    public void removeToken()
    {
        if (tokensExhausted())
            throw new IllegalStateException("Tried removing token that didn't exist");
        tokens.pop();
    }

    public boolean tokensExhausted()
    {
        return tokens.isEmpty();
    }

    public Token expect(ExpectParamsBuilder params) throws CompilationException
    {
        boolean succeeded = false;
        try
        {
            Token token;
            String message = params.getErrorMessage();

            boolean shouldIgnoreNewLine = params.isIgnoringNewLine();
            while ((token = requestToken(message)).content().equals(NEWLINE_ID) && shouldIgnoreNewLine)
                removeToken();

            if (token.content().equals(CONTINUE_LINE_ID))
                throw new UnexpectedTokenException("" + CONTINUE_LINE, token.location());

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
            {
                removeToken();
            }
        }
    }

    public Optional<Token> expectMaybe(ExpectParamsBuilder expectParamsBuilder) throws CompilationException
    {
        try
        {
            return Optional.of(expect(expectParamsBuilder));
        }
        catch (TokenExpectedException | UnexpectedEndException ex)
        {
            return Optional.empty();
        }
    }
}
