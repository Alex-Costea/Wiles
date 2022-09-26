package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

public class TokenCommand extends AbstractOperationComponent
{
    private final Token token;

    public TokenCommand(TokenTransmitter transmitter, Token token)
    {
        super(transmitter);
        this.token = token;
        inside = token.content();
    }

    public Token getToken()
    {
        return token;
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.TOKEN;
    }

    @Override
    public List<SyntaxTree> getComponents()
    {
        return new ArrayList<>();
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        return new CompilationExceptionsCollection();
    }

    @Override
    public String toString()
    {
        return inside;
    }
}
