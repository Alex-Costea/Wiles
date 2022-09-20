package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants.SYNTAX_TYPE;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.DECLARE_METHOD_ID;

public class ProgramCommand extends SyntaxTree
{
    private final List<MethodCommand> components = new ArrayList<>();
    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public ProgramCommand(TokenTransmitter transmitter)
    {
        super(transmitter);
    }

    @Override
    public SYNTAX_TYPE getType()
    {
        return SYNTAX_TYPE.PROGRAM;
    }

    @Override
    public List<MethodCommand> getComponents()
    {
        return components;
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            while (!transmitter.tokensExhausted())
            {
                expect(DECLARE_METHOD_ID);
                var methodCommand = new MethodCommand(transmitter);
                exceptions.add(methodCommand.process());
                components.add(methodCommand);
            }
        }
        catch (CompilationException ex)
        {
            exceptions.add(ex);
        }
        return exceptions;
    }
}
