package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

public class ParameterCommand extends SyntaxTree
{
    private final List<SyntaxTree> components=new ArrayList<>();

    private final CompilationExceptionsCollection exceptions=new CompilationExceptionsCollection();

    public ParameterCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.DECLARATION;
    }

    @Override
    public List<? extends SyntaxTree> getComponents()
    {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        return exceptions;
    }
}
