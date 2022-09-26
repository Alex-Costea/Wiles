package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.SIMPLE_TYPES;

public class TypeDefinitionCommand extends SyntaxTree
{
    private final List<SyntaxTree> components=new ArrayList<>();
    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();
    public TypeDefinitionCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.TYPE;
    }

    @Override
    public List<? extends SyntaxTree> getComponents()
    {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            Token token=expect(SIMPLE_TYPES::contains,"Type expected!");
            name=token.content();
        }
        catch (CompilationException e)
        {
            exceptions.add(e);
            if(!transmitter.tokensExhausted())
                transmitter.removeToken();
        }
        return exceptions;
    }
}
