package in.costea.wiles.services;

import in.costea.wiles.data.Token;
import in.costea.wiles.data.TokenLocation;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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

    //Make private
    public Token requestToken(String message) throws UnexpectedEndException
    {
        if (tokensExhausted()) throw new UnexpectedEndException(message, lastLocation);
        return tokens.getFirst();
    }

    //Make private
    public void removeToken()
    {
        if (tokensExhausted()) throw new IllegalStateException("Tried removing token that didn't exist");
        tokens.pop();
    }

    public boolean tokensExhausted()
    {
        return tokens.isEmpty();
    }

    public Token expect(Predicate<String> found, String message, @NotNull RemoveTokenEnum removeTokenWhen) throws CompilationException
    {
        boolean succeeded=false;
        try
        {
            Token token;
            while ((token = requestToken(message)).content().equals(NEWLINE_ID))
                removeToken();
            if (token.content().equals(CONTINUE_LINE_ID))
                throw new UnexpectedTokenException("" + CONTINUE_LINE, token.location());
            if (!found.test(token.content()))
                throw new TokenExpectedException(message, token.location());
            succeeded=true;
            return token;
        }
        finally
        {
            if((!succeeded && removeTokenWhen==RemoveTokenEnum.Always) ||
                    (succeeded && removeTokenWhen!=RemoveTokenEnum.Never))
            {
                removeToken();
            }
        }
    }

    public Token expect(Predicate<String> found, String message) throws CompilationException
    {
        return expect(found,message, RemoveTokenEnum.WhenFound);
    }

    public void expect(String expectedToken) throws CompilationException
    {
        expect(x -> Objects.equals(x, expectedToken), "Token \"" + TOKENS_INVERSE.get(expectedToken) + "\" expected!");
    }

    public Optional<Token> expectMaybe(Predicate<String> found) throws CompilationException
    {
        try
        {
            return Optional.of(expect(found, "Shouldn't happen"));
        }
        catch (TokenExpectedException | UnexpectedEndException ex)
        {
            return Optional.empty();
        }
    }

    public Optional<Token> expectMaybe(String expectedToken) throws CompilationException
    {
        return expectMaybe(x -> Objects.equals(x, expectedToken));
    }
}
