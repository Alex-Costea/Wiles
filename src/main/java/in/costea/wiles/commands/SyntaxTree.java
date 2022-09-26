package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.exceptions.UnexpectedTokenException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static in.costea.wiles.statics.Constants.*;

public abstract class SyntaxTree
{
    protected final TokenTransmitter transmitter;

    public SyntaxTree(TokenTransmitter transmitter)
    {
        this.transmitter = transmitter;

    }

    public abstract SYNTAX_TYPE getType();

    public abstract List<? extends SyntaxTree> getComponents();

    public abstract CompilationExceptionsCollection process();

    @Override
    public String toString()
    {
        return toString("");
    }

    protected Token expect(Predicate<String> found, String message) throws CompilationException
    {
        Token token;
        while ((token = transmitter.requestToken(message)).content().equals(NEWLINE_ID))
            transmitter.removeToken();
        if (token.content().equals(CONTINUE_LINE_ID))
            throw new UnexpectedTokenException("" + CONTINUE_LINE, token.location());
        if (!found.test(token.content()))
            throw new TokenExpectedException(message, token.location());
        transmitter.removeToken();
        return token;
    }

    protected void expect(String expectedToken) throws CompilationException
    {
        expect(x -> Objects.equals(x, expectedToken), "Token \"" + TOKENS_INVERSE.get(expectedToken) + "\" expected!");
    }

    protected Optional<Token> expectMaybe(Predicate<String> found) throws CompilationException
    {
        try
        {
            return Optional.of(expect(found, ""));
        }
        catch (TokenExpectedException ex)
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    protected Optional<Token> expectMaybe(String expectedToken) throws CompilationException
    {
        return expectMaybe(x -> Objects.equals(x, expectedToken));
    }

    protected void readUntilIgnoringErrors(Predicate<String> stop)
    {
        Token token;
        try
        {
            do
            {
                token = transmitter.requestToken("");
                if (stop.test(token.content()))
                    return;
                transmitter.removeToken();
            }
            while (true);
        }
        catch (UnexpectedEndException ignored)
        {
        }
    }

    public final String toString(String inside)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        if (!Objects.equals(inside, ""))
            sb.append(" ").append(inside).append(" ");
        if (getComponents().size() > 0)
        {
            sb.append("(");
            int i = 0;
            for (SyntaxTree component : getComponents())
            {
                sb.append(component.toString());
                if (i < getComponents().size() - 1)
                    sb.append("; ");
                i++;
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
