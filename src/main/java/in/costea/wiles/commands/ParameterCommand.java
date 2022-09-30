package in.costea.wiles.commands;

import in.costea.wiles.data.CompilationExceptionsCollection;
import in.costea.wiles.data.Token;
import in.costea.wiles.exceptions.CompilationException;
import in.costea.wiles.services.TokenTransmitter;
import in.costea.wiles.statics.Constants;

import java.util.List;

import static in.costea.wiles.builders.ExpectParamsBuilder.tokenOf;
import static in.costea.wiles.statics.Constants.COLON_ID;

public class ParameterCommand extends AbstractCommand
{
    private final TokenCommand tokenCommand;
    private TypeDefinitionCommand typeDefinition;

    private final CompilationExceptionsCollection exceptions = new CompilationExceptionsCollection();

    public ParameterCommand(TokenTransmitter transmitter, Token firstToken)
    {
        super(transmitter);
        tokenCommand=new TokenCommand(transmitter, firstToken);
    }

    @Override
    public Constants.SYNTAX_TYPE getType()
    {
        return Constants.SYNTAX_TYPE.DECLARATION;
    }

    @Override
    public List<AbstractCommand> getComponents()
    {
        return List.of(tokenCommand,typeDefinition);
    }

    @Override
    public CompilationExceptionsCollection process()
    {
        try
        {
            transmitter.expect(tokenOf(COLON_ID));
            typeDefinition = new TypeDefinitionCommand(transmitter);
            exceptions.add(typeDefinition.process());
        }
        catch (CompilationException e)
        {
            exceptions.add(e);
        }
        return exceptions;
    }
}
