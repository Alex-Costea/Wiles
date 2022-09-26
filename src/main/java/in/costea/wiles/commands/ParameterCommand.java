package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.ArrayList;
import java.util.List;

import static in.costea.wiles.statics.Constants.COLON_ID;

public class ParameterCommand extends SyntaxTree
{
    private final List<SyntaxTree> components = new ArrayList<>();

    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public ParameterCommand(TokenTransmitter transmitter, Token firstToken)
    {
        super(transmitter);
        components.add(new TokenCommand(transmitter, firstToken));
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
        try
        {
            expect(COLON_ID);
            var typeDefinitionCommand = new TypeDefinitionCommand(transmitter);
            exceptions.add(typeDefinitionCommand.process());
            components.add(typeDefinitionCommand);
        }
        catch (CompilationException e)
        {
            exceptions.add(e);
        }
        return exceptions;
    }
}
