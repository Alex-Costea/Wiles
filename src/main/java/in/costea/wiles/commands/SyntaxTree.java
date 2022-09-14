package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.TokenExpectedException;
import in.costea.wiles.exceptions.UnexpectedEndException;
import in.costea.wiles.services.TokenTransmitter;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static in.costea.wiles.statics.Constants.*;

public abstract class SyntaxTree {
    protected TokenTransmitter transmitter;

    public SyntaxTree(TokenTransmitter transmitter)
    {
        this.transmitter = transmitter;

    }
    public abstract SYNTAX_TYPE getType();
    public abstract List<? extends SyntaxTree> getComponents();

    public abstract CompilationExceptionsCollection process();

    @Override
    public String toString() {
        return toString("");
    }

    protected Token expect(Predicate<String> found, String message) throws TokenExpectedException, UnexpectedEndException {
        Token token;
        while((token = transmitter.requestToken(message)).content().equals(NEWLINE_ID))
            transmitter.removeToken();
        transmitter.removeToken();
        if(!found.test(token.content()))
            throw new TokenExpectedException(message,token.location());
        return token;
    }

    protected void expect(String expectedToken) throws TokenExpectedException, UnexpectedEndException {
        Token token;
        while((token = transmitter.requestTokenExpecting(expectedToken)).content().equals(NEWLINE_ID))
            transmitter.removeToken();
        if(!token.content().equals(expectedToken))
            throw new TokenExpectedException("Token \""+TOKENS_INVERSE.get(expectedToken)+"\" expected!",token.location());
        transmitter.removeToken();
    }

    protected void readRestOfLineIgnoringErrors()
    {
        Token token;
        try
        {
            do
            {
                token=transmitter.requestToken("");
                transmitter.removeToken();
            }
            while(!(token.content().equals(NEWLINE_ID) || token.content().equals(END_STATEMENT)));
        }
        catch (UnexpectedEndException ignored) {}
    }

    public final String toString(String inside)
    {
        StringBuilder sb=new StringBuilder();
        sb.append(getType());
        if(!Objects.equals(inside, ""))
            sb.append(" ").append(inside).append(" ");
        if(getComponents().size()>0)
        {
            sb.append("(");
            int i=0;
            for (SyntaxTree component : getComponents())
            {
                sb.append(component.toString());
                if(i<getComponents().size()-1)
                    sb.append("; ");
                i++;
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
